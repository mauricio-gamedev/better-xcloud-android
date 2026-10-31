# Cloud Hub

**Codinome de desenvolvimento:** Better xCloud Custom  
**Versão de desenvolvimento:** 0.2.0  
**Dispositivo de referência:** Samsung Galaxy A06 (Android 16)

O Cloud Hub é uma plataforma Android em desenvolvimento para reunir serviços de cloud gaming em uma única interface. O foco do catálogo é **free-first**: jogos free-to-play e formas legítimas de jogar sem assinatura aparecem antes das opções pagas.

> **Estado atual:** o APK agora inclui o userscript Better xCloud 6.7.12 e tenta integrá-lo ao Xbox Cloud por injeção no início da página via AndroidX WebKit. Essa integração é experimental e ainda precisa de validação real de login, controle, áudio, streaming e consumo de memória no Galaxy A06. GeForce NOW, Boosteroid e Blacknut continuam abrindo seus destinos oficiais externamente.

## Foco em jogos gratuitos

O projeto separa duas coisas que não podem ser confundidas:

1. **Custo do jogo:** free-to-play, promoção gratuita, incluído em assinatura, compra necessária ou não verificado.
2. **Custo do serviço:** plano gratuito, catálogo gratuito limitado, assinatura paga ou teste regional.

A tela inicial começa filtrada em jogos grátis. A lista só deve incluir títulos cuja condição gratuita e compatibilidade com pelo menos um provedor tenham sido verificadas. Por enquanto, Fortnite é o primeiro título confirmado para Xbox Cloud Gaming e GeForce NOW.

### Provedores no catálogo

| Serviço | Acesso ao serviço | Integração |
| --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita; catálogo maior exige plano elegível | Better xCloud embutido em WebView experimental |
| GeForce NOW Powered by ABYA | Plano gratuito disponível no Brasil; jogos comprados podem exigir posse na loja | Abre serviço oficial externamente |
| Boosteroid | Assinatura paga | Abre serviço oficial externamente |
| Blacknut | Assinatura; teste pode variar por região | Abre serviço oficial externamente |

A disponibilidade dos planos, jogos, sessões e servidores pode mudar por país e ao longo do tempo. A tela diferencia serviço gratuito de jogo gratuito.

## Integração Xbox / Better xCloud

- Userscript upstream fixado na revisão `f8397043f6d2148d2345d508902a38c69cf1ee20`.
- Versão incluída: `6.7.12`.
- Blob Git upstream: `05ad4c1e2d31caaaeaa2bcf3b7bd06d3c0f76a0f`.
- Injeção com `WebViewCompat.addDocumentStartJavaScript`, limitada à origem `https://www.xbox.com`.
- O script é executado somente no frame principal.
- Sem `addJavascriptInterface`: nenhum bridge genérico do JavaScript da página para métodos nativos é exposto.
- Se o WebView não suportar a injeção no início do documento, o app apresenta uma alternativa para abrir o Xbox no navegador.

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
