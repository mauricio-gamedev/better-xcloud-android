# Cloud Hub

**Codinome de desenvolvimento:** Better xCloud Custom  
**Status:** fundação inicial / protótipo Android  
**Dispositivo de referência:** Samsung Galaxy A06 (Android 16)

O Cloud Hub é uma plataforma Android em desenvolvimento para reunir diferentes serviços de cloud gaming em uma única interface. O Xbox Cloud Gaming será uma das integrações, não a única.

> **Estado atual:** esta primeira etapa entrega o esqueleto nativo do aplicativo, um catálogo de provedores e encaminhamento para os endereços oficiais. Ela ainda **não** incorpora as transmissões dentro do aplicativo nem implementa integração profunda com os provedores.

## Provedores no catálogo inicial

| Serviço | Estado nesta versão |
| --- | --- |
| Xbox Cloud Gaming | Link oficial; integração do núcleo Better xCloud planejada |
| GeForce NOW | Encaminhamento para o serviço oficial |
| Boosteroid | Encaminhamento para o serviço oficial |
| Blacknut | Encaminhamento para o serviço oficial |

A disponibilidade, os recursos de streaming e o método de acesso dependem de cada provedor, da região, do navegador e do dispositivo. O catálogo não representa uma parceria ou integração oficial.

## Arquitetura

- **Android app:** interface nativa em Kotlin.
- **Provider catalog:** metadados e estratégia de abertura de cada serviço.
- **Provider adapters:** módulos independentes a serem implementados conforme a compatibilidade e as regras de cada plataforma.
- **Xbox core:** integração futura baseada no userscript público do Better xCloud, mantendo os avisos de licença e atribuição.
- **CI:** compilação e testes automatizados pelo GitHub Actions, sem exigir Termux no telefone.

Veja [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) e [docs/PROVIDER-STATUS.md](docs/PROVIDER-STATUS.md).

## Compilar

O workflow `.github/workflows/android-build.yml` compila o APK de debug e executa os testes unitários em push, pull request ou execução manual.

Requisitos locais para compilar fora do CI:
- JDK 17
- Gradle 9.5.0
- Android SDK com a plataforma 36 e Build Tools 36.0.0

Comando:

```bash
gradle --no-daemon testDebugUnitTest assembleDebug
```

O APK de debug é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Princípios do projeto

1. Não armazenar credenciais de serviços de terceiros.
2. Não interceptar sessões, tokens ou autenticação dos provedores.
3. Não apresentar um serviço como integrado quando ele apenas abre um link externo.
4. Não forçar WebGPU, filtros de vídeo ou tarefas persistentes no Galaxy A06.
5. Validar cada integração em dispositivo real antes de promovê-la a integrada.
6. Preservar avisos, licenças e atribuições do código de terceiros utilizado.

## Relação com Better xCloud e Xbox

Este é um projeto independente e não oficial, sem afiliação com Microsoft, Xbox, NVIDIA, Boosteroid ou Blacknut. Better xCloud é um projeto de terceiros; qualquer código incorporado futuramente deverá manter os avisos e as condições da licença aplicável.

- [Better xCloud — código-fonte](https://github.com/redphx/better-xcloud)
- [Better xCloud para Android — repositório upstream](https://github.com/redphx/better-xcloud-android)
