# CRUD de Prontuários Médicos

Este é um projeto acadêmico de um sistema de **CRUD (Create, Read, Update, Delete)** para gerenciamento de pacientes e exames. O sistema foi desenvolvido para a disciplina de **Programação Orientada a Objetos (POO)** e utiliza **Java** para a interface de usuário e lógica de negócio, e **MySQL** para a persistência dos dados.

## Funcionalidades

O sistema permite que o usuário realize as seguintes operações:

### Pacientes

  * **Cadastrar** pacientes com Nome, CPF e Data de Nascimento.
  * **Listar** todos os pacientes cadastrados.
  * **Editar** os dados de um paciente existente.
  * **Deletar** um paciente.
  * **Associar** exames a um paciente.

### Exames

  * **Cadastrar** exames com Descrição, Data do exame e o paciente associado.
  * **Listar** todos os exames cadastrados.
  * **Editar** os dados de um exame existente.
  * **Deletar** um exame.

## Tecnologias Utilizadas

  * **Linguagem de Programação:** Java
  * **Interface Gráfica (GUI):** Java Swing
  * **Banco de Dados:** MySQL
  * **Padrão de Projeto:** DAO (Data Access Object)

## Modelo de Dados

O banco de dados é composto por duas tabelas: `PACIENTES` e `EXAMES`.

### Tabela `PACIENTES`

| Campo | Tipo de Dado | Restrições |
| :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO INCREMENT` |
| `cpf` | `VARCHAR(14)` | `UNIQUE`, `NOT NULL` |
| `nome` | `VARCHAR(255)` | `NOT NULL` |
| `data_nascimento` | `DATETIME` | `NOT NULL` |

### Tabela `EXAMES`

| Campo | Tipo de Dado | Restrições |
| :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY`, `AUTO INCREMENT` |
| `descricao` | `VARCHAR(255)` | `NOT NULL` |
| `data_exame` | `DATE` | `NOT NULL` |
| `paciente_id` | `BIGINT` | `FOREIGN KEY` (`PACIENTES.id`), `NOT NULL` |

## Como Executar

Para rodar o projeto, você precisa ter o **Java** e o **MySQL** instalados.

1.  **Baixe o arquivo `.jar`** do projeto neste link: [Download do Projeto](https://drive.google.com/drive/folders/1aNrcBuIQI0V4pr3eNylN04yKAWpCYoxZ?usp=sharing).
2.  **Configure o banco de dados:** Verifique as configurações de conexão com o MySQL em um arquivo de configuração (`.properties` ou similar) que o projeto deve ler.
3.  **Execute o arquivo `.jar`** através do terminal:
    ```bash
    java -jar Prontuario.jar
    ```
