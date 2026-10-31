# Arquitetura

## Objetivo

Aplicativo Android multi-provider para descobrir jogos, priorizar opções gratuitas e abrir serviços de cloud gaming. Cada provedor possui um adaptador e uma política de navegação próprios.

## Camadas

### Android app
- Interface nativa em Kotlin.
- Catálogo free-first com custo do jogo separado do custo do serviço.
- Registry de adaptadores por provedor.
- Xbox com WebView dedicada e userscript Better xCloud.
- GeForce NOW, Boosteroid e Blacknut com player WebView interno experimental e perfis independentes.
- Fallback externo para quando um serviço não aceitar a WebView ou exigir o aplicativo oficial.

### Catálogo de jogos

Os tipos de custo do jogo são `FREE_TO_PLAY`, `FREE_PROMOTION`, `SUBSCRIPTION_INCLUDED`, `PAID_PURCHASE` e `UNKNOWN`. O filtro "Só grátis" inclui apenas as duas primeiras categorias. O catálogo inicial tem 12 títulos free-to-play; disponibilidade regional e catálogo dos serviços podem mudar.

Cada registro guarda URLs por provedor, fontes de verificação, data de conferência e observações regionais. Não inferir compatibilidade cloud apenas por um jogo ser gratuito na loja.

## Adaptadores de provedores

`ProviderAdapterRegistry` escolhe a estratégia pelo ID:
- Xbox: `XboxCloudProviderAdapter` abre `XboxCloudActivity`.
- GeForce NOW, Boosteroid e Blacknut: `EmbeddedWebProviderAdapter` abre `CloudPlayerActivity` com um `ProviderWebProfile` próprio.
- Provedores sem player implementado usam `ExternalLinkProviderAdapter`.

O adaptador concentra requisitos do serviço: URL inicial, domínios confiáveis, autenticação e fallback. Um serviço só será marcado como estável após validar o fluxo real de streaming.

## Player interno

`ProviderWebProfiles` define para cada serviço URL de entrada e allowlist HTTPS. Hosts externos são encaminhados ao navegador do sistema; esquemas não HTTPS desconhecidos são bloqueados. Cookies de terceiros são habilitados na sessão WebView para autenticação federada. Nenhuma bridge JavaScript nativa é exposta.

`CloudPlayerActivity` cria uma única WebView sob demanda, oferece voltar/recarga/abertura externa, progresso, fullscreen e recuperação após encerramento do renderer. O título solicitado é mostrado como contexto, mas não é tratado como deep link quando o serviço não fornece um alvo específico verificado.

### GeForce NOW

A NVIDIA documenta streaming em dispositivos Android pelo Chrome em `play.geforcenow.com`. A incorporação Android WebView do Cloud Hub não é explicitamente certificada e permanece experimental. O player abre o catálogo geral; o usuário escolhe o jogo dentro do serviço.

### Boosteroid

O Boosteroid publica requisitos para telefone Android e navegador, incluindo Chrome atualizado, WebRTC e decodificação H.264 por hardware. O Cloud Hub abre a página oficial no player, mas a compatibilidade WebView, login e streaming precisam de teste físico. Alguns jogos sem suporte a controle podem exigir o app Android oficial ou periféricos adicionais.

### Blacknut

A Blacknut oferece um aplicativo web oficial em `https://app.blacknut.com/` para navegadores compatíveis. O Cloud Hub usa esse endereço no player interno como experimento; login, controles e vídeo precisam de validação no Galaxy A06.

## Xbox / Better xCloud

O userscript público Better xCloud 6.7.12 é empacotado localmente no APK, fixado no commit upstream `f8397043f6d2148d2345d508902a38c69cf1ee20`, blob `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`.

`XboxCloudActivity` verifica `WebViewFeature.DOCUMENT_START_SCRIPT`, injeta o userscript apenas na origem Xbox e impede inicialização em iframes. Login Microsoft e início de sessão continuam no fluxo oficial. Não há bridge `addJavascriptInterface`.

## Segurança

- Tráfego cleartext HTTP desativado.
- Safe Browsing ativo quando suportado.
- Acesso a arquivos locais desativado nas WebViews.
- Mixed content bloqueado.
- Hosts de navegação principal controlados por perfil.
- URLs HTTPS fora da allowlist são abertas externamente.
- O app não captura nem armazena senhas ou tokens.
- Não contornar DRM, fila, assinatura, região ou posse de jogos.

## Galaxy A06

- Criar apenas uma WebView quando o usuário abrir um serviço.
- Não manter players ocultos em segundo plano.
- Pausar e destruir WebView conforme o ciclo de vida.
- Não forçar WebGPU, filtros, resolução, bitrate ou FPS.
- Medir RAM, temperatura, latência, estabilidade, áudio e controle no aparelho real.

## Critérios de estabilidade

Para cada provedor, validar login, redirecionamentos, seleção e início/fim de jogo, áudio, controles Bluetooth, toque, fullscreen, rotação, retorno, retomada, consumo de RAM e recuperação do renderer. Até completar os testes, a integração permanece experimental.
