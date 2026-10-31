# Cloud Hub

**Codinome de desenvolvimento:** Better xCloud Custom  
**Versão de desenvolvimento:** 0.3.1  
**Dispositivo de referência:** Samsung Galaxy A06 (Android 16)

O Cloud Hub é uma plataforma Android em desenvolvimento para reunir serviços de cloud gaming em uma única interface. O foco é **free-first**: jogos free-to-play e formas legítimas de jogar sem comprar o título aparecem antes das opções pagas.

> **Estado atual:** o APK inclui o userscript Better xCloud 6.7.12 e tenta integrá-lo ao Xbox Cloud por injeção no início da página via AndroidX WebKit. A integração Xbox é experimental e ainda precisa de validação real de login, controle, áudio, streaming e consumo de memória no Galaxy A06. GeForce NOW, Boosteroid e Blacknut abrem seus destinos oficiais externamente.

## Catálogo grátis

A versão 0.3.0 traz **12 jogos free-to-play** no filtro inicial "Só grátis":

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

Fortnite é o único título da lista com acesso grátis verificado no Xbox Cloud e no GeForce NOW. Os outros 11 estão associados ao GeForce NOW; a compatibilidade foi conferida em um catálogo regional brasileiro com snapshot visível de 13/09/2026. A lista regional pode mudar, então o app exibe essa ressalva e o usuário deve confirmar a disponibilidade no serviço antes de iniciar.

A ABYA confirma plano gratuito no Brasil e mais de 75 jogos free-to-play; a listagem do app GeForce NOW na Google Play, atualizada em 28/10/2026, informa mais de 100 títulos grátis e cita Fortnite, Apex Legends e Destiny 2.

## Custo do jogo não é custo do serviço

O projeto mantém essas duas informações separadas:

1. **Custo do jogo:** free-to-play, promoção gratuita, incluído em assinatura, compra necessária ou não verificado.
2. **Custo do serviço:** plano gratuito, acesso gratuito a títulos selecionados, assinatura paga ou teste regional.

Assim, um jogo gratuito pode continuar exigindo uma conta ou plano do serviço, e um plano gratuito do serviço não transforma jogos pagos em gratuitos. O filtro "Só grátis" não inclui jogos que exigem assinatura ou compra individual.

## Provedores no catálogo

| Serviço | Acesso ao serviço | Integração |
| --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita; catálogo maior exige plano elegível | Better xCloud embutido em WebView experimental |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil; limites de fila e sessão | Abre serviço oficial externamente |
| Boosteroid | Assinatura paga | Abre serviço oficial externamente |
| Blacknut | Assinatura; teste pode variar por região | Abre serviço oficial externamente |

## Integração Xbox / Better xCloud

- Userscript upstream fixado na revisão `f8397043f6d2148d2345d508902a38c69cf1ee20`.
- Versão incluída: `6.7.12`.
- Blob Git upstream: `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`.
- Injeção com `WebViewCompat.addDocumentStartJavaScript`, limitada à origem `https://www.xbox.com`.
- O script é executado somente no frame principal.
- Sem `addJavascriptInterface`: nenhum bridge genérico do JavaScript da página para métodos nativos é exposto.
- Se o WebView não suportar a injeção no início do documento, o app oferece alternativa para abrir o Xbox no navegador.

O código-fonte do aplicativo Android oficial do Better xCloud não está neste repositório upstream; este projeto usa uma implementação Android própria e o userscript aberto, mantendo os avisos MIT. Consulte [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Compilar

O workflow `.github/workflows/android-build.yml` executa testes unitários e gera um APK de debug.

Requisitos locais:
- JDK 17
- Gradle 9.5.0
- Android SDK Platform 36 e Build Tools 36.0.0

Comando:

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

O APK de debug é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Arquitetura e validação

- [Arquitetura](docs/ARCHITECTURE.md)
- [Estado dos provedores](docs/PROVIDER-STATUS.md)
- [Política free-first](docs/FREE-FIRST-POLICY.md)

## Princípios do projeto

1. Não armazenar senhas, cookies ou tokens dos serviços em código próprio.
2. Não contornar DRM, fila, limite de plano ou restrições regionais.
3. Não marcar como integrado um serviço que só abre um link externo.
4. Não assumir que um jogo gratuito na loja também está disponível em cloud gaming.
5. Não forçar WebGPU, filtros gráficos ou tarefas persistentes no Galaxy A06.
6. Preservar licenças, avisos e atribuições de código de terceiros.

Este é um projeto independente e não oficial, sem afiliação com Microsoft, Xbox, NVIDIA, ABYA, Boosteroid ou Blacknut.

- [Better xCloud — código-fonte](https://github.com/redphx/better-xcloud)
- [Better xCloud para Android — repositório upstream](https://github.com/redphx/better-xcloud-android)
