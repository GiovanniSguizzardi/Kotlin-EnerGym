# EnerGym - App de Gestão Ecologica de Academias

> [!Anotation]
> Vídeo apresentando a aplicação:
> - [Link do Vídeo](https://www.youtube.com/watch?v=DXueBjSbfgk)

O **EnerGym** é um aplicativo Android desenvolvido para gestão de academias. Ele oferece funcionalidades como registro de academias, gerenciamento de usuários com sistema de pontos acumulativos através de leitura de QR-Code, e sincronização dos dados em tempo real com o Firebase. Este app é ideal para gerenciar academias, mantendo um registro atualizado e detalhado dos clientes e suas atividades.

## Funcionalidades Principais

### Gestão de Academias

- **Cadastro de Academias**: Permite adicionar novas academias com informações detalhadas como nome, endereço, cidade, estado e imagem. Todos os dados são sincronizados em tempo real no Firebase.
- **Edição e Exclusão de Academias**: O usuário pode editar ou excluir as informações de uma academia já cadastrada, com confirmação de exclusão para evitar ações acidentais.
- **Listagem de Academias**: Exibe uma lista de academias registradas, com a possibilidade de acessar detalhes e editar ou excluir registros.

### Sistema de Pontos com QR-Code

- **Leitura de QR-Code**: Ao acessar a página de detalhes de uma academia, o usuário pode iniciar uma leitura de QR-Code para iniciar a contagem de pontos.
- **Temporizador de Pontos**: Um temporizador começa a acumular pontos (100 pontos por minuto) automaticamente após a leitura do QR-Code. 
- **Encerrar Temporizador**: O usuário pode encerrar o temporizador manualmente, acumulando os pontos obtidos até o momento. Esses pontos são então atualizados no perfil do usuário.
- **Sincronização com Firebase**: Os pontos acumulados são salvos no Firebase em tempo real, para que possam ser acessados e mantidos mesmo após o fechamento do aplicativo.

### Sistema de Perfil do Usuário

- **Perfil com Sistema de Pontos**: Cada usuário tem um perfil que exibe os pontos acumulados, sincronizados com o Firebase.
- **Histórico de Registros de Academias**: Exibe o número de academias registradas no sistema.
- **Sincronização Automática**: O aplicativo usa o Firebase Realtime Database para manter os dados do usuário atualizados, incluindo os pontos acumulados.

---

## Estrutura do Projeto

- **Frontend**: Desenvolvido em **Kotlin**, o projeto utiliza componentes nativos do Android, garantindo uma UI intuitiva e fácil de usar.
- **Backend**: O **Firebase Realtime Database** é utilizado para armazenamento e sincronização em tempo real dos dados do aplicativo.
- **Armazenamento Offline com SharedPreferences**: O app utiliza `SharedPreferences` para armazenar o ID do usuário de forma segura, garantindo que os dados sejam acessíveis mesmo após o encerramento do aplicativo.
- **Lógica de Temporização e Acumulação de Pontos**: Através do uso de `Handler` e `Runnable` no Android, o sistema de temporização acumula pontos automaticamente a cada minuto enquanto o temporizador está ativo.

---

## Estrutura e Código

### Organização de Pastas e Arquivos

Para facilitar o desenvolvimento e manutenção, as funcionalidades foram organizadas em diferentes arquivos e pastas:

- `activity`: Contém as atividades principais como `AcademiaDetailsActivity`, responsável pela exibição e interação com os detalhes de uma academia.
- `fragments`: Inclui o `ProfileFragment`, responsável pela exibição do perfil do usuário, incluindo o sistema de pontos e histórico de academias.
- `models`: Define as classes de modelo, como `Academia`, que representam as academias no sistema.

### Principais Classes e Suas Responsabilidades

- **AcademiaDetailsActivity**: Exibe os detalhes de uma academia, permite iniciar e encerrar o temporizador de pontos, e atualizar os pontos no Firebase.
- **ProfileFragment**: Exibe o perfil do usuário com o total de pontos acumulados e o número de academias registradas. Também sincroniza os dados do usuário com o Firebase.
- **LoginActivity e RegisterActivity**: Controlam o fluxo de login e registro de novos usuários, armazenando o ID do usuário usando `SharedPreferences` para garantir uma experiência consistente.

---

## Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação principal utilizada para o desenvolvimento do app Android.
- **Firebase Realtime Database**: Usado para armazenamento em tempo real de dados do usuário e das academias.
- **OkHttp**: Biblioteca para realizar requisições HTTP ao Firebase, manipulando operações de CRUD (Create, Read, Update, Delete).
- **SharedPreferences**: Para armazenamento seguro do ID do usuário, garantindo acesso persistente a informações pessoais.
- **Android Jetpack**: Inclui componentes de Navegação e ViewBinding, que facilitam a navegação e a manipulação de componentes de UI.
- **ViewBinding**: Simplifica o acesso aos elementos de UI, reduzindo a repetição de código e tornando o código mais limpo.

---

## Como Usar

1. **Instale o aplicativo** em um dispositivo Android compatível.
2. **Registre-se ou faça login** para acessar o sistema. O ID do usuário será armazenado automaticamente para futuras sessões.
3. **Adicione academias** e visualize seus detalhes.

---

## Diagrama da aplicação
![image](https://github.com/user-attachments/assets/aa098972-6dd1-47ba-bff4-03cef432c8e1)

5. **Use o QR-Code**: Acesse uma academia e inicie o temporizador de pontos ao escanear o QR-Code.
6. **Verifique seu perfil** para acompanhar o total de pontos acumulados e o histórico de academias.

---
