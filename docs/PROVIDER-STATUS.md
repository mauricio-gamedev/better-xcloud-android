# Estado dos provedores

| Provedor | Modelo de acesso | Integração Cloud Hub | Estado |
| --- | --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita | Better xCloud + painel touch/performance nativo | Experimental; sessão, estabilidade e touch aguardam testes no A06 |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil, com limites | Player WebView interno + atalho app oficial | Experimental; modo de user-agent Android e logging precisam de teste |
| Boosteroid | Assinatura paga | Player WebView interno | Experimental; WebView precisa ser validada |
| Blacknut | Assinatura; teste pode variar | App web oficial no player interno | Experimental; WebView precisa ser validada |
| Steam Link | Gratuito, requer PC anfitrião | Abre app oficial Valve | Streaming local externo, não player Cloud Hub |

## Catálogo free-first

O catálogo inicial mantém 12 jogos free-to-play: Fortnite, Apex Legends, Destiny 2, Rocket League, Genshin Impact, Honkai: Star Rail, Wuthering Waves, Zenless Zone Zero, Marvel Rivals, Counter-Strike 2, Warframe e Dota 2.

Fortnite é o título com acesso gratuito verificado no Xbox Cloud deste catálogo. Os outros 11 estão associados ao GeForce NOW por snapshot regional brasileiro de 13/09/2026. A compatibilidade e a fila precisam ser reconfirmadas no serviço.

A comparação entre países ainda é uma tarefa de pesquisa, não uma permissão para trocar artificialmente de região. A NVIDIA informa que o catálogo é globalmente amplo, mas algumas classificações locais podem bloquear determinados jogos. Cada título novo precisa de verificação para o país do usuário, plano, loja e entitlement.

## Implementado nesta versão

- Xbox: perfil de tela cheia sem padding dos insets laterais; botão ⚙ abre controles touch, opacidade, estilo, resolução e estatísticas suportados pelo Better xCloud. Padrão inicial 720p para priorizar fluidez no A06.
- GeForce NOW: modo experimental de user-agent Android, remoção do aviso de jogo em uma faixa alta, header compacto, logging de console/HTTP e atalho ao app oficial.
- Steam Link: abre o app oficial Valve se instalado, ou o Google Play. O PC anfitrião executa o jogo e transmite para o A06.
- Boosteroid e Blacknut mantêm players web experimentais e allowlists independentes.

## Limites conhecidos

- O Cloud Hub não pode ampliar a duração de uma sessão imposta pelo Xbox nem eliminar fila/plano.
- 720p pode melhorar a estabilidade, mas não é garantia contra quedas se a rede/servidor oscilar.
- A compatibilidade de Chrome Android não certifica o WebView embutido para GFN, Boosteroid ou Blacknut.
- Steam Cloud sincroniza saves/configurações; não transmite nem executa jogos.
- Arquivos de jogos de PC, isoladamente, não fazem o A06 rodar GTA V. É necessário um host capaz de executar o jogo (PC) e um protocolo de streaming.

## Testes pendentes no Galaxy A06

- Xbox: bordas, modo imersivo, ajustes touch por jogo, áudio, resolução, estabilidade em partidas longas e uso de RAM.
- GFN: login, inicialização após login, catálogo, seleção, sessão, áudio, controle e fullscreen; comparar WebView experimental com app oficial.
- Boosteroid/Blacknut: login, catálogo, sessão, áudio e controles.
- Steam Link: pareamento com PC, Wi-Fi 5 GHz, controle e latência na rede local.

Nenhuma integração WebView será marcada como estável antes do teste real.
