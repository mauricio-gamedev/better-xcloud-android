# Estado dos provedores

| Provedor | Acesso gratuito | Estado de integração | Observações |
| --- | --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita | WebView experimental + Better xCloud 6.7.12 | Fortnite é o título grátis confirmado neste catálogo; a biblioteca maior exige plano elegível |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil | Acesso externo | ABYA confirma plano sem método de pagamento e 75+ F2P; app oficial lista 100+ F2P; sessões/fila podem ter limites |
| Boosteroid | Não há plano grátis padrão confirmado neste projeto | Acesso externo | Assinatura paga; compra do jogo pode ser necessária |
| Blacknut | Teste pode existir em ofertas/regiões específicas | Acesso externo | Catálogo principal exige assinatura |

## Catálogo de jogos

O catálogo free-first inicial contém 12 jogos:
- Fortnite (Xbox Cloud + GeForce NOW)
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

Fortnite é o único dos 12 com acesso gratuito verificado nos dois provedores. Os outros 11 usam GeForce NOW. A compatibilidade regional dos 11 foi conferida no diretório regional do GeForce NOW Brasil, cujo snapshot visível está datado de 13/09/2026; a disponibilidade pode mudar. O usuário deve confirmar o jogo na biblioteca do serviço antes de iniciar.

O custo do jogo é separado do custo do serviço:
- Um jogo F2P ainda pode ter itens opcionais pagos.
- Uma membership Free não torna todos os jogos do GeForce NOW gratuitos.
- Títulos pagos ou incluídos em assinatura não entram no filtro "Só grátis".

## O que está efetivamente implementado

- Xbox: userscript Better xCloud incluído no APK e configurado para injeção no início de documentos da origem Xbox. A integração é experimental até testes no dispositivo.
- GeForce NOW, Boosteroid e Blacknut: o app abre a URL HTTPS oficial. Não existe streaming incorporado nesses três módulos.
- Os registros de jogo guardam links de preço e compatibilidade; a disponibilidade é revalidada no serviço antes de jogar.

## Testes pendentes no Galaxy A06

- Login Microsoft, incluindo redirecionamento de autenticação.
- Início e término de sessão.
- Áudio, toque e controle Bluetooth.
- Rotação, botão voltar e retomada.
- Uso de RAM e comportamento quando o Android encerra o processo do WebView.
- Fallback em versões do WebView sem `DOCUMENT_START_SCRIPT`.

Nenhuma integração deve ser marcada como estável antes desses testes.
