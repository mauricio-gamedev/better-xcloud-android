# Estado dos provedores

| Provedor | Acesso gratuito | Estado de integração | Observações |
| --- | --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita | WebView experimental + Better xCloud 6.7.12 | O catálogo maior exige plano elegível; validar login e streaming no A06 |
| GeForce NOW Powered by ABYA | Plano gratuito no Brasil | Acesso externo | Plano Free tem fila/anúncios/limites; muitos jogos exigem posse na loja |
| Boosteroid | Não há plano grátis padrão confirmado neste projeto | Acesso externo | Assinatura paga; compra do jogo pode ser necessária |
| Blacknut | Teste pode existir em ofertas/regiões específicas | Acesso externo | Catálogo principal exige assinatura |

## O que está efetivamente implementado

- Xbox: o userscript Better xCloud está incluído no APK e configurado para injeção no início de documentos da origem Xbox. A integração é experimental até os testes no dispositivo.
- Demais serviços: o app apenas abre a URL HTTPS oficial pelo Android. Não existe streaming incorporado nesses módulos.
- Catálogo de jogos: Fortnite é o primeiro título free-to-play confirmado para Xbox Cloud e GeForce NOW. Não adicionar títulos apenas com base na classificação "grátis" da loja; a compatibilidade cloud precisa ser verificada.

## Testes pendentes no Galaxy A06

- Login Microsoft, incluindo redirecionamento de autenticação.
- Início e término de sessão.
- Áudio, toque e controle Bluetooth.
- Rotação, botão voltar e retomada.
- Uso de RAM e comportamento quando o Android encerra o processo do WebView.
- Fallback em versões do WebView sem `DOCUMENT_START_SCRIPT`.

Nenhuma integração deve ser marcada como estável antes desses testes.
