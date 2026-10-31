# Estado dos provedores

| Provedor | Modelo de acesso | Integração no Cloud Hub | Estado |
| --- | --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos selecionados com conta Microsoft gratuita | WebView própria + Better xCloud 6.7.12 | Experimental; login e streaming ainda precisam de validação no A06 |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil, com limites de fila/sessão | Player WebView interno experimental | Catálogo abre dentro do app; WebView não é explicitamente certificada pela NVIDIA |
| Boosteroid | Assinatura paga | Abre site/app oficial externamente | Adaptador externo até validar streaming Android interno |
| Blacknut | Assinatura; teste pode variar por oferta/região | Abre site/app oficial externamente | Adaptador externo até validar streaming Android interno |

## Catálogo free-first

O catálogo inicial contém 12 jogos free-to-play:
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

Fortnite é o único com acesso grátis verificado nos dois provedores deste catálogo; os outros 11 estão associados ao GeForce NOW a partir de uma verificação regional datada de 13/09/2026. A disponibilidade deve ser confirmada dentro do serviço. Jogo free-to-play não significa que todo plano do serviço garanta acesso imediato.

## O que está implementado

- Xbox: Better xCloud é injetado no início da origem Xbox. Fortnite usa a rota de produto Xbox `BT5P2X999VH2`; o usuário ainda precisa fazer login e pressionar Jogar.
- GeForce NOW: existe um adaptador próprio e o novo player abre `https://play.geforcenow.com/` dentro do Cloud Hub. O título escolhido é mostrado como contexto. O catálogo geral é aberto; o usuário precisa escolher o jogo no serviço.
- Boosteroid e Blacknut: abrem os destinos oficiais externamente. Não são apresentados como players internos.

## Limites técnicos conhecidos

A NVIDIA documenta streaming por Chrome em dispositivos Android, mas isso não garante compatibilidade do Android WebView embutido. O app oficial Android continua sendo a experiência recomendada pelo provedor. O Cloud Hub não contorna filas, planos, região, autenticação, DRM ou propriedade do jogo.

Ainda não há deep link Android por jogo verificado para os registros GFN deste catálogo. Não inventar identificadores ou rotas para simular início automático.

## Testes pendentes no Galaxy A06

- Xbox: login, retorno de autenticação, início/fim de sessão, áudio, toque e controle Bluetooth.
- GFN: login federado, catálogo, seleção de jogo, sessão, áudio, controle, fullscreen e retorno.
- Ambos: rotação, retomada, encerramento do renderer, fallback, consumo de RAM e estabilidade após várias partidas.

Nenhum player será marcado como estável antes de completar os testes em aparelho físico.
