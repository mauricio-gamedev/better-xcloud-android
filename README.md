# Cloud Hub

**Codinome de desenvolvimento:** Better xCloud Custom  
**Versão de desenvolvimento:** 0.4.2  
**Dispositivo de referência:** Samsung Galaxy A06 (Android 16)

O Cloud Hub é uma plataforma Android independente para reunir serviços de cloud gaming em uma única interface. O foco é **free-first**: jogos free-to-play e formas legítimas de jogar sem comprar o título aparecem antes das opções pagas.

> **Estado atual:** Xbox usa Better xCloud 6.7.12 em WebView experimental, com controles touch e qualidade configuráveis no app. GeForce NOW, Boosteroid e Blacknut usam adaptadores e player WebView experimental; o GFN tem modo de user-agent de compatibilidade, diagnóstico e atalho para o app Android oficial. Steam Link foi adicionado para transmitir de um PC anfitrião na rede local.

## Xbox Cloud: touch e desempenho

O botão de engrenagem na tela Xbox permite:
- disponibilidade dos controles touch: Automático, Sempre disponível ou Desativado;
- opacidade dos controles;
- estilo padrão, suave ou branco;
- resolução 720p, 1080p, 1080p HQ ou Automático;
- estatísticas do stream.

Essas opções configuram preferências suportadas pelo Better xCloud original; o Cloud Hub não substitui nem falsifica os eventos de controle. O perfil inicial usa 720p e opacidade de 80% como ponto de partida para o Galaxy A06. Ajuste conforme sua rede e estabilidade.

O Xbox mantém limites de sessão, fila e plano definidos pela Microsoft. O Cloud Hub não remove nem contorna esses limites.

## GeForce NOW e outros players

O GFN mantém a opção interna. A nova compatibilidade de user-agent remove os marcadores específicos de WebView para testar o comportamento do site como navegador Android. Isso é experimental: a NVIDIA documenta Chrome no Android e recomenda o app oficial para a melhor experiência. O botão **APP** abre o GeForce NOW oficial instalado, ou sua página no Google Play.

O player interno também serve a Boosteroid e Blacknut, com allowlists HTTPS independentes. Streaming real dentro do WebView ainda precisa de validação em dispositivo físico; não é considerado estável apenas porque a tela de login abre.

## Catálogo free-first

O catálogo inicial contém 12 jogos free-to-play: Fortnite, Apex Legends, Destiny 2, Rocket League, Genshin Impact, Honkai: Star Rail, Wuthering Waves, Zenless Zone Zero, Marvel Rivals, Counter-Strike 2, Warframe e Dota 2.

Fortnite é o título com acesso grátis verificado no Xbox Cloud deste catálogo. Os demais estão associados ao GeForce NOW por uma verificação regional brasileira com snapshot de 13/09/2026. A disponibilidade pode mudar; confirme no serviço.

O próximo trabalho de catálogo é comparar mercados e classificações regionais. Não adicionamos títulos apenas porque aparecem em um catálogo estrangeiro: a região, o plano, a loja e a classificação etária podem impedir o acesso.

## Steam Link, Steam Cloud e jogos locais

O cartão **Steam Link • PC na rede local** abre o aplicativo oficial da Valve. O fluxo requer:
- um PC capaz de executar o jogo, com Steam aberto;
- o celular e o PC na mesma rede local para o modo doméstico;
- preferencialmente PC ligado ao roteador por Ethernet e A06 no Wi-Fi de 5 GHz.

Steam Link envia imagem/áudio do PC ao telefone e devolve os comandos. **Steam Cloud é diferente:** sincroniza saves e configurações; não executa jogos nem substitui um PC anfitrião.

O Cloud Hub ainda não possui um servidor local próprio. Um futuro host baseado em Sunshine/Moonlight pode ser avaliado para quem tiver um PC compatível. Os arquivos de GTA V ou outro jogo, sozinhos, não fazem o Galaxy A06 executar um jogo Windows de alta exigência.

## Provedores

| Serviço | Acesso | Integração |
| --- | --- | --- |
| Xbox Cloud Gaming | Alguns títulos com conta Microsoft gratuita; planos pagos têm seus próprios limites | Better xCloud + WebView experimental |
| GeForce NOW Powered by ABYA | Membership gratuita no Brasil, com limites | Player WebView interno experimental + app oficial |
| Boosteroid | Assinatura paga | Player WebView interno experimental |
| Blacknut | Assinatura; teste pode variar | Aplicativo web oficial no player interno experimental |
| Steam Link | Gratuito, com PC anfitrião | Abre o app oficial para streaming local |

## Segurança e arquitetura

- Cada provedor web tem allowlist HTTPS própria.
- Mixed content e tráfego HTTP simples permanecem bloqueados.
- Não há bridge JavaScript nativa genérica.
- O app não captura nem armazena senhas ou tokens.
- Nenhum limite de fila, DRM, plano, região ou propriedade do jogo é contornado.
- Players WebView continuam experimentais até login, streaming, áudio, controles, fullscreen e memória serem testados no A06.

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

Este é um projeto independente, sem afiliação com Microsoft, Xbox, NVIDIA, ABYA, Boosteroid, Blacknut ou Valve.
