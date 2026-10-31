# Arquitetura inicial

## Objetivo

Oferecer uma interface Android única para descobrir e abrir serviços de cloud gaming, evoluindo gradualmente para integrações profundas onde houver suporte técnico, compatibilidade e permissão.

O projeto não tenta substituir as plataformas de autenticação nem transportar sessões entre provedores.

## Camadas

### Android app

Aplicativo nativo em Kotlin. Responsável por:
- navegação e interface comum;
- catálogo e preferências locais;
- encaminhamento seguro para serviços oficiais;
- diagnóstico do dispositivo;
- integração futura com módulos de provedor.

### Catálogo e contrato de provedor

Cada serviço deve declarar:
- identificador estável;
- nome e descrição;
- URL ou destino oficial;
- modalidade de integração;
- capacidades realmente suportadas;
- limitações e observações por dispositivo/região.

Um item no catálogo não significa que o serviço esteja integrado dentro do app.

### Adaptadores de provedores

As integrações serão isoladas por provedor. Modalidades previstas:

| Modalidade | Uso |
| --- | --- |
| `EXTERNAL_HANDOFF` | Abre o app oficial, quando o Android resolver o link, ou o navegador |
| `IN_APP_WEB` | WebView somente após validação de suporte, login, controles e política do serviço |
| `CUSTOM_SCRIPT` | Script próprio ou userscript autorizado, como a futura integração do núcleo Better xCloud |
| `NATIVE_REMOTE_PLAY` | Integração com recursos oficiais de Remote Play, quando tecnicamente viável |

### Núcleo Xbox

O código do userscript Better xCloud será mantido como módulo separado da interface e do catálogo. A integração exige:
1. fixar uma revisão upstream;
2. revisar a licença e preservar avisos/atribuições;
3. definir o ponto de injeção no mecanismo WebView;
4. testar login, navegação, controle, áudio, vídeo e retomada;
5. validar uso de memória e estabilidade no Galaxy A06.

O código-fonte do aplicativo Android upstream não está presente neste repositório. Não devemos tratar este projeto como um fork compilável do app oficial.

## Regras de segurança

- Nunca pedir ou armazenar senha de terceiros.
- Não extrair cookies, tokens ou credenciais de outros apps.
- Não contornar DRM, fila, limites de plano ou controles de região.
- Preferir destinos HTTPS oficiais.
- Não injetar scripts em domínios não autorizados.
- Tratar erros de abertura e provedores indisponíveis sem travar a interface.

## Regras de desempenho para o Galaxy A06

- Não iniciar serviços persistentes sem necessidade.
- Não manter WebViews de provedores em segundo plano.
- Evitar filtros gráficos e renderizadores alternativos por padrão.
- Não forçar resolução, FPS ou bitrate sem medir a rede e a compatibilidade.
- Medir memória, temperatura, estabilidade e latência antes de promover uma otimização.

## Critério para marcar uma integração como concluída

Uma integração só pode ser marcada como integrada após:
- fluxo de abertura e autenticação testado;
- streaming iniciado e encerrado corretamente;
- controle, áudio e orientação de tela verificados;
- tratamento de erro e retorno ao catálogo;
- teste no Galaxy A06 e documentação das limitações.

Até lá, o catálogo deve exibir claramente que o serviço usa acesso externo ou que a integração está planejada.
