# Arquitetura

## Objetivo

Aplicativo Android multi-provider para descobrir jogos, priorizar opções gratuitas e abrir serviços de cloud gaming. Cada provedor possui um adaptador e uma política de navegação próprios.

## Adaptadores

- Xbox: `XboxCloudProviderAdapter` abre `XboxCloudActivity` com o userscript Better xCloud 6.7.12.
- GeForce NOW, Boosteroid e Blacknut: `EmbeddedWebProviderAdapter` abre `CloudPlayerActivity` com `ProviderWebProfile` dedicado.
- Steam Link: `SteamLinkProviderAdapter` abre o app oficial Valve instalado, ou o Google Play quando não estiver instalado.
- Outros provedores usam `ExternalLinkProviderAdapter`.

## Xbox / Better xCloud

O userscript upstream permanece fixado em `f8397043f6d2148d2345d508902a38c69cf1ee20` (6.7.12). O script é injetado em document-start apenas no frame principal da origem Xbox; nenhuma bridge JavaScript nativa é exposta.

As preferências do painel nativo são persistidas em SharedPreferences e copiadas, no document-start, para os objetos de armazenamento local usados pelo Better xCloud:
- `BetterXcloud`: disponibilidade, opacidade e estilo dos controles touch, resolução alvo;
- `BetterXcloud.Stream`: estatísticas durante a sessão.

A injeção preserva as outras chaves já existentes nesses objetos. O painel usa opções já implementadas pelo Better xCloud e não adiciona uma camada falsa de gamepad. O catálogo de layouts touch personalizados continua sob controle do Better xCloud e de seus contribuidores.

### Perfil inicial para Galaxy A06

720p, controles touch disponíveis para todos os jogos, opacidade de 80%, estilo padrão e estatísticas desativadas. O usuário pode alterar resolução, opacidade, estilo e disponibilidade. Nenhum bitrate/FPS é forçado pelo Cloud Hub.

A atividade Xbox usa immersive fullscreen e deixa o WebView ocupar a tela inteira; os insets da barra lateral do Android não são aplicados como padding ao vídeo. As barras do sistema podem ser reveladas temporariamente por gesto.

## Player web / GeForce NOW

A `CloudPlayerActivity` compartilha o shell de navegação, cookies, progresso, fullscreen, ciclo de vida e recuperação do renderer. GFN usa um perfil que remove os marcadores `wv` e `Version/4.0` do user-agent, como experimento de compatibilidade. O perfil não altera os outros serviços. Console JS e erros HTTP de navegação principal são registrados para diagnóstico.

O player interno permanece experimental: suporte a Chrome Android não certifica Android WebView. O atalho APP abre `com.nvidia.geforcenow` ou a página oficial da Play Store. A autenticação e a sessão continuam sob controle do serviço.

## Steam Link e Steam Cloud

Steam Link é um cliente externo oficial para transmitir do PC anfitrião para Android. O PC deve executar Steam e o jogo; a rede recomendada é PC por Ethernet e telefone em Wi-Fi de 5 GHz.

Steam Cloud sincroniza saves/configurações, mas não é um serviço de streaming nem um servidor que executa jogos. Os arquivos locais de um título não bastam para reproduzir um jogo Windows no A06.

Um host próprio futuro pode usar um PC compatível com Sunshine como servidor e Moonlight como cliente Android, sujeito a validação de encoder, rede, controles e segurança. Isso ainda não está implementado no Cloud Hub.

## Catálogo e disponibilidade

Custo de jogo e custo de serviço são dimensões separadas. O catálogo só deve incorporar jogos com fontes por provedor/mercado e data de verificação. Catálogo de outra região não deve ser tratado como automaticamente disponível no Brasil. Classificação etária, licenças, entitlement, loja e plano podem alterar a disponibilidade.

## Segurança e limites

- HTTP cleartext e mixed content bloqueados.
- Cada WebView tem allowlist HTTPS própria.
- Sem `addJavascriptInterface` genérico.
- Sem captura de credenciais/tokens.
- Não contornar DRM, fila, plano, região ou verificação de posse.
- Não forçar WebGPU, codec, resolução maior, bitrate ou FPS no A06.

## Validação física

No A06, validar login, seleção e início/fim de sessão, áudio, touch e controles Bluetooth, fullscreen, rotação, retomada, uso de RAM, renderer encerrado, estabilidade em partidas longas e comportamento após várias entradas/saídas. Players WebView só podem ser marcados como estáveis depois desses testes.
