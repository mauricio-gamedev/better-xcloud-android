# Cloud Hub

**Codinome de desenvolvimento:** Better xCloud Custom  
**Versão de desenvolvimento:** 0.4.1  
**Dispositivo de referência:** Samsung Galaxy A06 (Android 16)

O Cloud Hub é uma plataforma Android independente para reunir serviços de cloud gaming em uma única interface. O foco é **free-first**: jogos free-to-play e formas legítimas de jogar sem comprar o título aparecem antes das opções pagas.

> **Estado atual:** Xbox usa Better xCloud 6.7.12 em WebView experimental. GeForce NOW, Boosteroid e Blacknut têm adaptadores próprios e compartilham um player WebView experimental, cada um com URL inicial e allowlist HTTPS independentes. A integração embutida não é certificada pelos provedores e precisa de testes reais no Galaxy A06. O app mantém opção de abrir o serviço no navegador oficial quando necessário.

## Catálogo grátis

O catálogo inicial tem 12 jogos free-to-play: Fortnite, Apex Legends, Destiny 2, Rocket League, Genshin Impact, Honkai: Star Rail, Wuthering Waves, Zenless Zone Zero, Marvel Rivals, Counter-Strike 2, Warframe e Dota 2.

Fortnite é o único título com acesso grátis verificado no Xbox Cloud e no GeForce NOW. Os outros 11 estão associados ao GeForce NOW por uma verificação regional brasileira com snapshot de 13/09/2026. A disponibilidade pode mudar; confirme dentro do serviço.

## Custo do jogo não é custo do serviço

O app mantém separados o custo do jogo (free-to-play, promoção, assinatura, compra ou não verificado) e o custo do serviço (plano grátis, acesso gratuito a títulos selecionados, assinatura ou teste). Um jogo grátis pode exigir uma conta/plano específico do serviço.

## Provedores

| Serviço | Acesso | Integração |
| --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita | Better xCloud em WebView experimental |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil, com limites | Player WebView interno experimental |
| Boosteroid | Assinatura paga | Player WebView interno experimental |
| Blacknut | Assinatura; teste pode variar | Aplicativo web oficial no player interno experimental |

## Player interno e adaptadores

O registry encaminha cada serviço ao adaptador correto. GeForce NOW, Boosteroid e Blacknut usam uma Activity compartilhada, com URL inicial e allowlist HTTPS próprias, barra de navegação, recarga, fullscreen, progresso e recuperação do renderer. Hosts fora da allowlist são abertos no navegador; esquemas desconhecidos são bloqueados.

A NVIDIA documenta streaming por Chrome no Android; o Boosteroid publica requisitos de navegador Android, WebRTC e H.264; o Blacknut fornece um app web oficial em `https://app.blacknut.com/`. Isso não garante compatibilidade do Android WebView embutido. Cada player continua experimental até login e streaming serem testados no A06.

O player GFN abre o catálogo geral. Ainda é necessário selecionar o jogo dentro do serviço, porque não há deep link Android por título verificado neste catálogo.

## Xbox / Better xCloud

- Userscript upstream fixado na revisão `f8397043f6d2148d2345d508902a38c69cf1ee20`.
- Versão: `6.7.12`.
- Blob SHA: `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`.
- Injeção com `WebViewCompat.addDocumentStartJavaScript`, limitada à origem `https://www.xbox.com`.
- Sem `addJavascriptInterface`; nenhum bridge genérico do JavaScript para métodos nativos.

O projeto é independente e não oficial. Consulte [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).

## Compilar

O workflow `.github/workflows/android-build.yml` executa testes unitários e gera um APK debug.

Requisitos: JDK 17, Gradle 9.5.0, Android SDK Platform 36 e Build Tools 36.0.0.

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Documentação

- [Arquitetura](docs/ARCHITECTURE.md)
- [Estado dos provedores](docs/PROVIDER-STATUS.md)
- [Política free-first](docs/FREE-FIRST-POLICY.md)

## Princípios

1. Não armazenar senhas, tokens ou cookies de outros apps.
2. Não contornar DRM, fila, limites de plano ou restrições regionais.
3. Não alegar integração quando há apenas redirecionamento externo.
4. Não presumir compatibilidade por o jogo ser gratuito na loja.
5. Não forçar WebGPU, filtros, resolução ou FPS no Galaxy A06.
6. Preservar licenças e atribuições de código de terceiros.

Este é um projeto independente, sem afiliação com Microsoft, Xbox, NVIDIA, ABYA, Boosteroid ou Blacknut.

- [Better xCloud — código-fonte](https://github.com/redphx/better-xcloud)
- [Better xCloud para Android — repositório upstream](https://github.com/redphx/better-xcloud-android)
