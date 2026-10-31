# Arquitetura

## Objetivo

Aplicativo Android multi-provider para descobrir jogos, priorizar opções gratuitas e abrir serviços de cloud gaming. Cada provedor possui um adaptador independente.

## Camadas

### Android app
- Interface nativa em Kotlin.
- Biblioteca local de jogos com filtros free-first.
- Catálogo e status de custo dos serviços.
- Encaminhamento externo para provedores sem integração interna.
- Tela WebView dedicada ao Xbox Cloud Gaming.

### Catálogo de jogos

O custo do jogo é independente do custo do serviço. Os tipos atuais são:
- `FREE_TO_PLAY`
- `FREE_PROMOTION`
- `SUBSCRIPTION_INCLUDED`
- `PAID_PURCHASE`
- `UNKNOWN`

A ordenação free-first usa essa prioridade. O filtro "Só grátis" inclui somente `FREE_TO_PLAY` e `FREE_PROMOTION`. Títulos sem verificação de disponibilidade em cloud não podem ser adicionados só porque aparecem como gratuitos na loja.

A versão 0.3.0 contém 12 títulos free-to-play:
- Fortnite
- Apex Legends
- Destiny 2
- Rocket League
- Genshin Impact
- Honkai: Star Rail
- Wuthering Waves
- Zenless Zone Zero
- Marvel Rivals
- Counter-Strike 2
- Warframe
- Dota 2

Cada registro guarda URL de lançamento por provedor, links das fontes consultadas, data da verificação e uma nota de disponibilidade regional. Para os 11 títulos exclusivos do GeForce NOW, a compatibilidade foi conferida no snapshot de um diretório regional do Brasil, com data visível de 13/09/2026. Como o diretório é terceiro e o catálogo pode mudar, a nota é exibida no card e a disponibilidade deve ser reconfirmada dentro do serviço.

### Custo do serviço

Cada provedor declara seu modelo de acesso:
- jogos gratuitos com conta sem assinatura;
- plano gratuito com limitações;
- assinatura paga;
- assinatura com teste regional eventual.

Essa informação não altera o custo individual dos jogos. GeForce NOW Powered by ABYA oferece membership gratuita no Brasil, mas muitos jogos pagos ainda exigem posse na loja. A ABYA informa mais de 75 jogos free-to-play; o app oficial no Google Play lista mais de 100 e destaca Fortnite, Apex Legends e Destiny 2.

### Adaptadores de provedores

`ProviderAdapterRegistry` escolhe a estratégia pelo identificador do provedor:
- Xbox: `XboxCloudProviderAdapter` abre `XboxCloudActivity`.
- GeForce NOW, Boosteroid e Blacknut: `ExternalLinkProviderAdapter` abre os destinos HTTPS oficiais.

Integrações internas adicionais devem ser implementadas em módulos próprios. Não presumir que uma WebView genérica substitua o app oficial de um serviço.

## Xbox / Better xCloud

O userscript público Better xCloud 6.7.12 é empacotado localmente no APK e fixado na revisão upstream:

- Repositório: `redphx/better-xcloud`
- Commit: `f8397043f6d2148d2345d508902a38c69cf1ee20`
- Arquivo: `dist/better-xcloud.user.js`
- Blob SHA: `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`

`XboxCloudActivity`:
1. Verifica `WebViewFeature.DOCUMENT_START_SCRIPT`.
2. Lê o userscript empacotado no APK.
3. Registra a injeção com `WebViewCompat.addDocumentStartJavaScript`, limitada à origem `https://www.xbox.com`.
4. Impede que o userscript inicialize em iframes.
5. Permite navegação interna apenas nos domínios Xbox/Microsoft/Microsoft login necessários.
6. Abre outros destinos HTTPS no navegador do sistema.
7. Destrói a WebView ao encerrar a atividade para liberar memória.

Não é exposta uma bridge `addJavascriptInterface`. Isso evita oferecer a páginas de login e conteúdo web uma interface nativa genérica. Consequentemente, recursos do AppInterface do app Android oficial (atalhos nativos, ponte de vibração, pointer server, atalhos e captura nativa) ainda não estão implementados.

Se a feature de injeção não existir no WebView instalado, a tela oferece fallback externo. O app não baixa JavaScript remoto em tempo de execução.

## Segurança

- Permissão de rede limitada ao uso da WebView e abertura de serviços.
- Tráfego cleartext HTTP desativado.
- Safe Browsing mantido ativo quando suportado.
- Injeção de script limitada à origem Xbox.
- Nenhuma bridge nativa JavaScript genérica.
- Navegação principal externa encaminhada ao navegador do sistema.
- Nenhum armazenamento próprio de senha, token ou cookie de outro app.

## Galaxy A06

- Uma WebView Xbox é criada somente quando solicitada.
- Não manter WebViews dos provedores abertas em segundo plano.
- Destruir WebView em `onDestroy`.
- Não forçar filtros, WebGL2 ou WebGPU.
- Não forçar resolução, FPS ou bitrate.
- Medir memória, temperatura, latência, estabilidade, áudio e controle em dispositivo físico.

## Critério para marcar Xbox como estável

- Login Microsoft e retorno ao Xbox validados.
- Usuário consegue iniciar e encerrar um jogo.
- Áudio, controle Bluetooth, toque e orientação testados.
- Nenhuma falha crítica de navegação.
- Reentrada e destruição da WebView sem crescimento de memória.
- Testes em Galaxy A06 com a versão real do Android System WebView.

Até que esses testes sejam concluídos, a integração permanece marcada como experimental.
