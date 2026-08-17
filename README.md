# Cidade em Dia

Aplicação desktop acadêmica para registro, acompanhamento e gestão de
reclamações urbanas em Santa Rita do Sapucaí/MG. O projeto foi desenvolvido em
Java 17 com JavaFX e utiliza dados em memória, sem banco de dados.

## O que existe no projeto

### Autenticação e perfis

A tela de login utiliza três usuários fixos de demonstração. Depois da
autenticação, a interface e as opções do menu são ajustadas conforme o perfil.

| Perfil | Usuário | Senha | Criar reclamação | Gerenciar reclamações | Ver mapa principal |
|---|---|---|---:|---:|---:|
| Administrador | `admin` | `1234` | Sim | Sim | Sim |
| Atendente | `atendente` | `1234` | Não | Sim | Sim |
| Cidadão | `cidadao` | `1234` | Sim | Apenas as próprias | Sim |

O logout exige confirmação e, quando concluído, retorna ao login com uma
notificação em toast.

### Reclamações

- cadastro com categoria, subcategoria opcional dependente, prioridade, descrição e localização;
- busca de endereço restrita ao contexto de Santa Rita do Sapucaí;
- seleção de localização em uma janela de mapa;
- inclusão de até quatro imagens PNG ou JPEG, limitadas a 5 MB cada;
- edição e cancelamento pelo cidadão enquanto a reclamação estiver pendente;
- justificativa obrigatória para cancelamento;
- alteração de status por administrador ou atendente;
- histórico de cadastro, alterações e mudanças de status;
- janela de acompanhamento com progresso, etapa atual e última atualização;
- navegação entre o problema selecionado e os demais problemas públicos da cidade;
- visualização das imagens anexadas e do local no mapa;
- dados de demonstração com endereços de Santa Rita do Sapucaí.

Os status disponíveis são: `Pendente`, `Em análise`, `Em execução`, `Resolvido`
e `Cancelado`. As prioridades são: `Baixa`, `Média`, `Alta` e `Urgente`.
As categorias possuem um catálogo fixo de subcategorias opcionais. Entre elas está
`Trânsito e mobilidade`, com opções para semáforo, sinalização, faixa de
pedestres, veículo abandonado e congestionamento recorrente.

### Consulta e exportação

A lista de reclamações possui:

- pesquisa por descrição, endereço, categoria ou subcategoria;
- filtros por categoria, subcategoria, status, prioridade e período de datas;
- identificação visual de status e prioridade;
- abertura dos detalhes por botão ou clique duplo;
- ações de mudança de status para os perfis de gestão;
- exportação dos registros filtrados para CSV (UTF-8).

O cidadão visualiza somente reclamações associadas ao seu identificador. A
exportação CSV e as ações administrativas ficam disponíveis apenas para os
perfis que podem gerenciar reclamações.

### Painéis e mapa

O painel administrativo apresenta quantidade total, pendentes, em andamento,
resolvidas e urgentes, além de taxa de resolução, gráfico por categoria,
gráfico por prioridade e uma lista de reclamações urgentes recentes.

O painel do cidadão apresenta os totais das próprias reclamações e suas
atividades recentes.

O mapa principal exibe marcadores das reclamações cadastradas, diferenciados
por prioridade. A seleção por clique é habilitada somente quando o mapa é
aberto pelo formulário para escolher uma localização.

### Configurações e acessibilidade

- informações do usuário autenticado;
- seleção entre texto padrão, grande e extragrande;
- atalhos de navegação por teclado;
- confirmação de logout;
- tela com informações sobre o sistema.

Atalhos disponíveis:

| Atalho | Destino |
|---|---|
| `Alt+1` | Início |
| `Alt+2` | Nova reclamação, quando permitida |
| `Alt+3` | Reclamações |
| `Alt+4` | Mapa, quando permitido |
| `Alt+5` | Configurações |
| `Alt+0` | Sobre o sistema |

## Tecnologias e serviços

- Java 17;
- JavaFX 17 (`controls`, `fxml` e `web`);
- FXML e CSS para as interfaces;
- Maven para dependências e execução;
- `org.json` para interpretar respostas da geocodificação;
- Leaflet 1.9.4 e mapas do OpenStreetMap;
- Nominatim/OpenStreetMap para busca e geocodificação reversa;
- JUnit Jupiter 5.10.2 e Maven Surefire 3.2.5 para testes.

## Requisitos

- JDK 17;
- Apache Maven disponível no terminal;
- conexão com a internet para carregar o Leaflet, os blocos do OpenStreetMap e realizar buscas de endereço.

## Como executar

Na raiz do projeto:

```bash
mvn clean javafx:run
```

## Testes automatizados

A suíte possui 35 testes unitários distribuídos entre modelos, serviço de
reclamações, permissões e sessão do usuário.

```bash
mvn test
```

Cobertura funcional da suíte:

- criação e histórico inicial de reclamações;
- mudanças de status e responsável pela alteração;
- progresso e mensagens de acompanhamento de cada status;
- edição dos dados da reclamação;
- vínculo e validação entre categorias e subcategorias;
- inclusão, substituição e imutabilidade da lista de anexos;
- carregamento e filtragem dos mocks por perfil;
- filtro por período com datas inclusivas e limites opcionais;
- inclusão de reclamações na coleção em memória;
- dados e senha do usuário;
- matriz de permissões dos três perfis;
- rótulos dos enums usados na interface;
- armazenamento e encerramento da sessão;
- validação de login nulo;
- leitura e alteração de uma localização.

Os testes são unitários e não abrem janelas, não carregam o mapa e não fazem
requisições ao Nominatim ou a outros serviços externos.

## Dados e limitações

- Não há banco de dados.
- Usuários e reclamações iniciais são mocks carregados em memória.
- Reclamações criadas durante o uso são perdidas ao encerrar a aplicação.
- Os anexos são referenciados pelo caminho local do arquivo original.
- As credenciais são fixas e existem somente para demonstração acadêmica.
- Mapa e busca de endereço podem ficar indisponíveis sem conexão com a internet.

## Estrutura do projeto

```text
src
├── main
│   ├── java/org/example
│   │   ├── controller   # controladores JavaFX
│   │   ├── model        # reclamação, usuário, localização e enums
│   │   ├── service      # mocks e regras de acesso às reclamações
│   │   └── util         # sessão, navegação, mapa, acessibilidade e toasts
│   └── resources
│       ├── css          # estilos das telas
│       ├── images       # logo e imagens
│       ├── map          # página Leaflet incorporada ao JavaFX WebView
│       ├── view         # telas FXML
└── test/java/org/example
    ├── model            # testes dos modelos, enums e permissões
    ├── service          # testes do serviço de reclamações
    └── util             # testes da sessão do usuário
```

## Natureza do projeto

Projeto acadêmico, versão 1.0, voltado à demonstração de conceitos de Java,
JavaFX, arquitetura em camadas, controle de acesso e testes unitários.
