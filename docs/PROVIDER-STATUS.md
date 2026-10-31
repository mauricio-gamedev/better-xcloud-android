# Estado das integrações

Esta tabela reflete o que existe no código nesta etapa. "Acesso externo" significa apenas abrir um endereço oficial por meio do Android; não significa streaming incorporado ao Cloud Hub.

| Provedor | Estado atual | Próxima etapa |
| --- | --- | --- |
| Xbox Cloud Gaming | Acesso externo implementado | Portar e integrar o núcleo userscript Better xCloud |
| GeForce NOW | Acesso externo implementado | Validar app oficial, navegador, gamepad e fluxo de retorno |
| Boosteroid | Acesso externo implementado | Validar app oficial e navegador no A06 |
| Blacknut | Acesso externo implementado | Confirmar disponibilidade regional e validar app/navegador no A06 |

## Critérios para avançar

- **Acesso externo:** o link HTTPS oficial abre e o Cloud Hub continua utilizável ao retornar.
- **Integração Web interna:** autenticação, vídeo, áudio, gamepad, toque, rotação, retomada e encerramento foram testados.
- **Integração nativa:** fluxo suportado oficialmente e sem depender de APIs privadas ou credenciais extraídas de outros aplicativos.

Nenhum provedor está marcado como streaming integrado nesta versão.
