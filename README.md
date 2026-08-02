# Cidade em dia

Sistema acadêmico de gestão de reclamações urbanas desenvolvido em Java 17 e
JavaFX. A aplicação permite registrar ocorrências, acompanhar seu andamento,
visualizar os locais no mapa e apoiar a equipe municipal com indicadores e
priorização operacional.

## Funcionalidades

- autenticação com perfis e permissões diferentes;
- registro e edição de reclamações com endereço, prioridade e fotos;
- identificação de reclamações atrasadas ou próximas do vencimento;
- fluxo validado de alteração de status;
- histórico de status e observações administrativas;
- pesquisa e filtros por categoria, status, prioridade e atraso;
- exportação dos registros filtrados em CSV UTF-8;
- dashboard com indicadores, gráficos e fila de atenção;
- mapa com as reclamações cadastradas;
- interface adaptada ao perfil do usuário;
- tamanho de texto ajustável, redução de animações e atalhos de teclado;
- notificações em toast e tratamento centralizado de falhas;
- confirmação segura de logout.

## Usuários de demonstração

| Perfil | Usuário | Senha | Acesso principal |
|---|---|---|---|
| Administrador | `admin` | `1234` | Acesso completo |
| Atendente | `atendente` | `1234` | Gestão, indicadores e mapa |
| Cidadão | `cidadao` | `1234` | Cadastro e acompanhamento próprio |

## Tecnologias

- Java 17
- JavaFX 17
- FXML e CSS
- Maven
- OpenStreetMap e Leaflet
- Nominatim para geocodificação
- JUnit 5

## Como executar

Pré-requisitos: JDK 17 e Maven instalados.

```bash
mvn clean javafx:run
```

A busca de endereços depende de conexão com a internet. O mapa e a lista local
de ruas continuam disponíveis conforme os recursos carregados pela aplicação.

## Testes

```bash
mvn test
```

Os testes cobrem protocolo, prazos, atrasos, histórico e transições permitidas
entre os status das reclamações.

## Atalhos

- `Alt+1`: início
- `Alt+2`: nova reclamação
- `Alt+3`: reclamações
- `Alt+4`: mapa
- `Alt+5`: configurações
- `Alt+6`: central de acompanhamento
- `Alt+0`: sobre o sistema

## Dados

Este projeto não utiliza banco de dados. Os usuários e as reclamações iniciais
são mocks mantidos em memória para fins de demonstração acadêmica. Novos dados
existem apenas durante a execução atual da aplicação.

## Estrutura principal

```text
src/main/java/org/example
├── controller   # comportamento das telas
├── model        # entidades e enums
├── service      # regras de negócio e mocks
└── util         # navegação, sessão, mapa, acessibilidade e notificações

src/main/resources
├── css          # identidade visual das telas
├── view         # arquivos FXML
├── map          # página do mapa
└── images       # logo e imagens da aplicação
```
