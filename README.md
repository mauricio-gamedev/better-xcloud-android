# Cloud Hub

**Codinome de desenvolvimento:** Better xCloud Custom  
**Versão de desenvolvimento:** 0.4.0  
**Dispositivo de referência:** Samsung Galaxy A06 (Android 16)

O Cloud Hub é uma plataforma Android independente para reunir serviços de cloud gaming em uma única interface. O foco é **free-first**: jogos free-to-play e formas legítimas de jogar sem comprar o título aparecem antes das opções pagas.

> **Estado atual:** Xbox usa Better xCloud 6.7.12 em WebView experimental. GeForce NOW agora tem adaptador próprio e abre seu catálogo dentro de um player WebView experimental do Cloud Hub. A NVIDIA documenta streaming via Chrome no Android, mas não certifica explicitamente WebView embutido; a integração precisa ser testada no Galaxy A06. Boosteroid e Blacknut continuam externos até existir validação específica.

## Catálogo grátis

O catálogo inicial possui 12 jogos free-to-play: Fortnite, Apex Legends, Destiny 2, Rocket League, Genshin Impact, Honkai: Star Rail, Wuthering Waves, Zenless Zone Zero, Marvel Rivals, Counter-Strike 2, Warframe e Dota 2.

Fortnite é o único título com acesso grátis verificado no Xbox Cloud e no GeForce NOW. Os outros 11 estão associados ao GeForce NOW por uma verificação regional brasileira com snapshot visível de 13/09/2026. A lista pode mudar; confirme a disponibilidade dentro do serviço.

## Custo do jogo não é custo do serviço

O app mantém separados:
1. **Custo do jogo:** free-to-play, promoção gratuita, incluído em assinatura, compra necessária ou não verificado.
2. **Custo do serviço:** plano gratuito, acesso gratuito a títulos selecionados, assinatura paga ou teste regional.

Um jogo grátis pode exigir conta/plano específico do serviço; uma membership gratuita não transforma jogos pagos em gratuitos.

## Provedores

| Serviço | Acesso | Integração |
| --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita | Better xCloud em WebView experimental |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil, com limites | Player WebView interno experimental; catálogo geral |
| Boosteroid | Assinatura paga | Abertura externa |
| Blacknut | Assinatura; teste pode variar | Abertura externa |

## Player interno e adaptadores

O registry encaminha cada provedor ao seu adaptador. O player comum centraliza navegação, cookies da sessão, fullscreen, retorno, recarga, progresso e recuperação do renderer. Cada provedor possui uma allowlist HTTPS própria; hosts externos são abertos no navegador e esquemas desconhecidos são bloqueados.

O player GFN abre o catálogo geral. Ainda é necessário selecionar o jogo dentro do serviço, porque não há deep link Android por título verificado neste catálogo. Compatibilidade do Android WebView não é garantida pelo suporte oficial de Chrome Android.

## Xbox / Better xCloud

- Userscript upstream fixado na revisão `f8397043f6d2148d2345d508902a38c69cf1ee20`.
- Versão: `6.7.12`.
- Blob SHA: `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`.
- Injeção com `WebViewCompat.addDocumentStartJavaScript`, limitada à origem `https://www.xbox.com`.
- Sem `addJavascriptInterface`; nenhum bridge genérico do JavaScript para métodos nativos.

O projeto é independente e não oficial. Consulte [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) para as atribuições e licenças.

## Compilar

O workflow `.github/workflows/android-build.yml` executa testes unitários e gera um APK debug.

Requisitos: JDK 17, Gradle 9.5.0, Android SDK Platform 36 e Build Tools 36.0.0.

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

O APK é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Documentação

- [Arquitetura](docs/ARCHITECTURE.md)
- [Estado dos provedores](docs/PROVIDER-STATUS.md)
- [Política free-first](docs/FREE-FIRST-POLICY.md)

## Princípios

1. Não armazenar senhas, tokens ou cookies de outros apps.
2. Não contornar DRM, fila, limites de plano ou restrições regionais.
3. Não alegar integração quando há apenas redirecionamento externo.
4. Não presumir compatibilidade de um jogo por ele ser gratuito na loja.
5. Não forçar WebGPU, filtros gráficos, resolução ou FPS no Galaxy A06.
6. Preservar licenças e atribuições de código de terceiros.

Este é um projeto independente, sem afiliação com Microsoft, Xbox, NVIDIA, ABYA, Boosteroid ou Blacknut.

- [Better xCloud — código-fonte](https://github.com/redphx/better-xcloud)
- [Better xCloud para Android — repositório upstream](https://github.com/redphx/better-xcloud-android)
