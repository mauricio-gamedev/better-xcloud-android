# Estado dos provedores

| Provedor | Modelo de acesso | Integração Cloud Hub | Estado |
| --- | --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita | WebView própria + Better xCloud 6.7.12 | Experimental; login e streaming aguardam validação no A06 |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil, com limites | Player WebView interno | Experimental; catálogo geral, integração precisa de teste |
| Boosteroid | Assinatura paga | Player WebView interno | Experimental; Chrome/WebRTC Android é documentado, WebView não está certificada |
| Blacknut | Assinatura; teste pode variar | Aplicativo web oficial no player interno | Experimental; login, controle e streaming aguardam validação |

## Catálogo free-first

O catálogo inicial contém 12 jogos free-to-play: Fortnite, Apex Legends, Destiny 2, Rocket League, Genshin Impact, Honkai: Star Rail, Wuthering Waves, Zenless Zone Zero, Marvel Rivals, Counter-Strike 2, Warframe e Dota 2.

Fortnite é o único com acesso grátis verificado nos dois provedores do catálogo; os outros 11 estão associados ao GeForce NOW por uma verificação regional brasileira com snapshot de 13/09/2026. A disponibilidade muda e deve ser confirmada dentro do serviço. Jogo free-to-play não significa que todo plano do serviço garante acesso imediato.

## Implementado

- Xbox: Better xCloud é injetado no início da origem Xbox. Fortnite abre a rota de produto `BT5P2X999VH2`; login e confirmação de Jogar continuam sob controle do Xbox.
- GeForce NOW: adaptador próprio abre `https://play.geforcenow.com/` no player interno. O usuário seleciona o jogo no catálogo do serviço; não há deep link por título verificado neste app.
- Boosteroid: adaptador próprio abre a homepage oficial no player interno. Requisitos oficiais de navegador Android incluem Chrome atualizado, WebRTC e decodificação H.264; o WebView do Cloud Hub ainda precisa ser validado.
- Blacknut: adaptador próprio abre o app web oficial `https://app.blacknut.com/`. A compatibilidade do WebView embutido ainda precisa ser validada.
- Os três players web compartilham a Activity, mas cada um tem uma allowlist de hosts e URL inicial separadas.
- Se a WebView não funcionar, o usuário pode abrir a página no navegador ou app oficial, quando disponível.

## Limites técnicos

Suporte a Chrome Android não implica certificação de Android WebView. Nenhum player contorna autenticação, DRM, fila, planos, limites regionais ou posse do jogo. O app oficial do serviço pode ser necessário para controles, determinados tipos de jogo ou recursos nativos.

## Testes pendentes no Galaxy A06

- Xbox: login, retorno de autenticação, início/fim de sessão, áudio, toque e controle Bluetooth.
- GeForce NOW: login, catálogo, seleção, sessão, áudio, controle, fullscreen e retorno.
- Boosteroid: autenticação, catálogo, controles compatíveis, WebRTC, decodificação e vídeo.
- Blacknut: login, catálogo, emparelhamento de controle, vídeo, áudio e sessão.
- Todos: rotação, retomada, renderer encerrado, fallback, consumo de RAM e estabilidade após várias partidas.

Nenhum player será marcado como estável antes dos testes em aparelho físico.
