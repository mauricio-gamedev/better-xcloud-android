# Arquitetura

## Objetivo

Aplicativo Android multi-provider para descobrir jogos, priorizar opções gratuitas e abrir serviços de cloud gaming. Cada provedor possui um adaptador e uma política de navegação próprios.

## Camadas

### Android app
- Interface nativa em Kotlin.
- Catálogo free-first com custo do jogo separado do custo do serviço.
- Registry de adaptadores por provedor.
- Xbox com WebView dedicada e userscript Better xCloud.
- GeForce NOW com player WebView interno experimental.
- Boosteroid e Blacknut permanecem em abertura externa até validação específica no Android.

### Catálogo de jogos

Os tipos de custo do jogo são `FREE_TO_PLAY`, `FREE_PROMOTION`, `SUBSCRIPTION_INCLUDED`, `PAID_PURCHASE` e `UNKNOWN`. O filtro "Só grátis" inclui apenas as duas primeiras categorias. A versão 0.3.x iniciou com 12 títulos free-to-play; disponibilidade regional e catálogo dos serviços podem mudar.

Cada registro guarda URLs por provedor, fontes de verificação, data de conferência e observações regionais. Não é permitido inferir que um jogo é compatível com cloud apenas por ser gratuito na loja.

## Adaptadores de provedores

`ProviderAdapterRegistry` escolhe a estratégia pelo ID:
- Xbox: `XboxCloudProviderAdapter` abre `XboxCloudActivity`.
- GeForce NOW: `GeForceNowProviderAdapter` abre `CloudPlayerActivity` e entrega o título selecionado como contexto.
- Boosteroid e Blacknut: `ExternalLinkProviderAdapter` abre a URL HTTPS oficial.

O adaptador é a fronteira que deve concentrar os requisitos específicos do serviço: URL inicial, domínios confiáveis, autenticação, compatibilidade de sessão e fallback. Um serviço só deve ser marcado como integrado depois de validar o fluxo real de streaming.

## Player interno / GeForce NOW

A NVIDIA documenta streaming em dispositivos Android pelo Chrome em `play.geforcenow.com`. O Cloud Hub usa a mesma entrada oficial dentro de um Android WebView, mas essa incorporação não é explicitamente certificada pela NVIDIA e permanece experimental. O player não mascara user-agent nem força resolução, codec, FPS ou WebGPU.

`ProviderWebProfiles` valida o destino e mantém uma allowlist de hosts HTTPS do serviço e de provedores comuns de autenticação. Navegações para hosts fora da lista são encaminhadas ao navegador do sistema; esquemas não HTTPS desconhecidos são bloqueados. Cookies de terceiros são habilitados somente na sessão WebView para permitir autenticação federada. Nenhuma bridge JavaScript nativa é exposta.

`CloudPlayerActivity` cria uma única WebView sob demanda, mostra o nome do provedor e o título solicitado, oferece voltar/recarga/abertura externa, acompanha progresso, trata fullscreen e destrói a WebView ao encerrar. Se o renderer morrer, mostra uma tela de recuperação.

O GFN ainda abre o catálogo geral. Não há deep link Android por jogo confirmado no catálogo atual; por isso, o jogador seleciona o título dentro do próprio serviço. A WebView interna precisa ser validada no Galaxy A06 antes de ser considerada estável.

## Xbox / Better xCloud

O userscript público Better xCloud 6.7.12 é empacotado localmente no APK, fixado no commit upstream `f8397043f6d2148d2345d508902a38c69cf1ee20`, blob `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`.

`XboxCloudActivity` verifica `WebViewFeature.DOCUMENT_START_SCRIPT`, lê o userscript local, injeta com `WebViewCompat.addDocumentStartJavaScript` apenas na origem Xbox e impede inicialização em iframes. Login Microsoft permanece no fluxo oficial; não há bypass de autenticação ou entitlement. Não é exposta uma bridge `addJavascriptInterface`.

Se a injeção no início do documento não estiver disponível, o app oferece fallback externo. O APK não baixa JavaScript remoto em tempo de execução.

## Segurança

- Tráfego cleartext HTTP desativado.
- Safe Browsing ativo quando suportado.
- JavaScript nativo sem bridge genérica.
- Acesso a arquivos locais e conteúdo local desativado nas WebViews.
- Mixed content bloqueado.
- Hosts de navegação principal controlados por perfil de provedor.
- URLs HTTPS fora da allowlist são abertas externamente.
- O app não captura nem armazena senhas, tokens ou cookies de outros apps.

## Galaxy A06

- Criar apenas uma WebView quando o usuário abrir um serviço.
- Não manter players ocultos em segundo plano.
- Pausar e destruir a WebView conforme o ciclo de vida da Activity.
- Não forçar filtros, WebGL2, WebGPU, resolução, bitrate ou FPS.
- Medir RAM, temperatura, latência, estabilidade, áudio e controle no aparelho real.

## Critérios de estabilidade

Para cada provedor, validar login, redirecionamentos, seleção e início/término de jogo, áudio, controles Bluetooth, toque, fullscreen, rotação, retorno, retomada, consumo de RAM e recuperação após encerramento do renderer. Até completar esses testes, a integração permanece experimental.
