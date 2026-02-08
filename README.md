# ToDo List with Firebase

Uma aplicação Android moderna para gerenciar tarefas com autenticação por Firebase e armazenamento em Firestore.

## 🎯 Objetivo

Demonstrar integração completa de:

- Firebase Authentication (Email/Password)
- Cloud Firestore (sincronização em tempo real)
- Jetpack Compose (UI declarativa)
- Arquitetura MVVM com Clean Architecture
- Kotlin Coroutines e Flows reativos

---

## 🚀 Funcionalidades

- ✅ Cadastro de usuários com email/senha via Firebase Authentication
- ✅ Login seguro com validação de credenciais
- ✅ Criar, ler, atualizar e deletar tarefas (CRUD completo)
- ✅ Marcar tarefas como concluídas/não concluídas
- ✅ Armazenamento em tempo real com Firestore
- ✅ Sincronização automática de dados entre dispositivos
- ✅ Dados persistem por usuário (privacidade garantida)
- ✅ Interface moderna com Jetpack Compose
- ✅ Navegação fluida entre telas
- ✅ Logout com limpeza de sessão

---

## 🏗️ Arquitetura

O projeto utiliza **Clean Architecture** com **MVVM**, separando responsabilidades em três camadas:

### Estrutura de Pacotes

```
presentation/
├── auth/              # UI de autenticação (LoginScreen, SignUpScreen)
├── tasks/             # UI de tarefas (TaskListScreen)
└── navigation/        # Navegação entre telas (NavGraph)

domain/
├── model/             # Entidades de negócio (Task, User)
└── repository/        # Interfaces de repositórios

data/
└── repository/        # Implementações com Firebase
```

### MVVM

- **Model**: Data classes (Task, User) definidas em `domain/model`
- **View**: Composables (LoginScreen, TaskListScreen, etc)
- **ViewModel**: StateFlow gerenciando estado reativo

### Fluxo de Dados

```
User Input → ViewModel → Repository → Firebase → Flow → ViewModel → UI Update (recomposição)
```

### Padrões Utilizados

- **flatMapLatest + stateIn**: Manter listeners do Firestore ativos automaticamente
- **callbackFlow**: Converter callbacks Firebase em Flows reativos
- **collectAsState()**: Observar StateFlow em Composables

---

## 📱 Screenshots

### Tela de Login

Autenticação com email e senha, validação em tempo real, exibição de erros.

### Tela de Sign Up

Cadastro com validação de email, força de senha e confirmação.

### Tela de Lista de Tarefas

Listagem com checkbox, edição em linha, deletion com confirmação.

### Diálogos

AlertDialogs para adicionar, editar e confirmar deleção de tarefas.

---

## 🛠️ Tecnologias Utilizadas

| Categoria            | Tecnologia                            |
| -------------------- | ------------------------------------- |
| **Linguagem**        | Kotlin 1.8+                           |
| **UI Framework**     | Jetpack Compose                       |
| **Arquitetura**      | Clean Architecture + MVVM             |
| **Backend**          | Firebase (Authentication + Firestore) |
| **Navegação**        | Jetpack Navigation Compose            |
| **Async**            | Kotlin Coroutines + Flow              |
| **State Management** | StateFlow + MutableStateFlow          |
| **Build System**     | Gradle KTS                            |
| **Target SDK**       | Android 14 (API 34)                   |
| **Min SDK**          | Android 7.0 (API 24)                  |

---

## ⚙️ Setup - Firebase

### 1. Criar Projeto Firebase

1. Acesse [console.firebase.google.com](https://console.firebase.google.com)
2. Clique em "Criar projeto" ou "Add Project"
3. Nome: `TodoListFirebase`
4. Selecione sua região/país
5. Clique em "Criar projeto"

### 2. Registrar App Android

1. No Firebase Console, clique em "Adicionar app"
2. Selecione "Android"
3. Preencha:
   - **Package name**: `com.example.todolistwithfirebase`
   - **App nickname**: TodoListFirebase
4. Clique em "Registrar app"
5. **Download do `google-services.json`**
   - Clique em "Download google-services.json"
   - Salve o arquivo em local seguro

### 3. Colocar google-services.json no Projeto

1. Copie o arquivo `google-services.json`
2. Cole na pasta `app/` do projeto
   - Estrutura: `TodoListFirebase/app/google-services.json`

### 4. Habilitar Authentication

1. No Firebase Console, vá em **Authentication**
2. Clique em **Get started** (se primeira vez)
3. Selecione **Email/Password**
4. Ative a opção "Email/Password"
5. Clique em **Save**

### 5. Criar Firestore Database

1. Vá em **Firestore Database**
2. Clique em **Create database**
3. Selecione localização (próxima a você)
4. Para iniciar: **Start in test mode** (segurança relaxada para testes)
5. Clique em **Enable**

### 6. Configurar Firestore Security Rules

1. No Firestore, vá em aba **Rules**
2. Substitua o conteúdo por:

```firestore
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Apenas usuários autenticados podem ler tarefas
    match /tasks/{document=**} {
      allow read: if request.auth != null;
      allow create: if request.auth != null && request.resource.data.userId == request.auth.uid;
      allow update: if request.auth != null && resource.data.userId == request.auth.uid;
      allow delete: if request.auth != null && resource.data.userId == request.auth.uid;
    }
  }
}
```

3. Clique em **Publish**

---

## 🚀 Como Executar

### Pré-requisitos

- **Android Studio Flamingo** ou superior
- **JDK 11+**
- **Android SDK 24+** (API 24 = Android 7.0)
- **Gradle 7.0+**

### Passos

#### 1. Clonar Repositório (se aplicável)

```bash
git clone https://github.com/seu_usuario/TodoListFirebase.git
cd TodoListFirebase
```

#### 2. Configurar Firebase

Siga a seção **Setup - Firebase** acima

#### 3. Abrir em Android Studio

- File → Open → TodoListFirebase
- Aguarde sincronização Gradle

#### 4. Executar

- Clique em **Run** (verde) ou pressione **Shift+F10**
- Selecione:
  - **Emulador Android** (se não tiver, crie um em AVD Manager)
  - **Dispositivo físico** (se conectado via USB)

#### 5. Testar Funcionalidades

1. **Sign Up**: Crie conta com novo email/senha
   - Valide força de senha (mín. 6 caracteres)
   - Valide confirmação de senha

2. **Login**: Faça login com as credenciais criadas

3. **CRUD de Tarefas**:
   - Clique no botão flutuante (+) para adicionar tarefa
   - Clique no ícone de edição para editar
   - Clique no ícone de lixo para deletar (confirme)
   - Clique na checkbox para marcar como concluída

4. **Persistência**:
   - Logout e faça login novamente
   - Verifique se as tarefas persistem

5. **Sincronização em Tempo Real**:
   - Abra o app em dois dispositivos
   - Crie/edite/delete tarefa em um
   - Observe atualização em tempo real no outro

---

## 🔍 Debugging

### Logcat Filters

Para debugar problemas, use filtros no Logcat:

```
TaskRepository     # Operações com Firestore
TaskViewModel      # Gerenciamento de estado
AuthViewModel      # Autenticação
AuthRepository     # Login/Signup
```

### Problemas Comuns

#### PERMISSION_DENIED no Firestore

**Causa**: Regras de segurança incorretas ou usuário não autenticado
**Solução**: Verifique se está logado e as regras estão publicadas

#### Tarefas desaparecem da UI

**Causa**: Listener cancelado prematuramente
**Solução**: App mantém listeners ativos com `stateIn` + `flatMapLatest`

#### Email não valida

**Causa**: Validação simples pode estar falhando
**Solução**: App usa `Patterns.EMAIL_ADDRESS` para validação confiável

---

## 📚 Estrutura de Arquivos

```
app/
├── src/main/java/com/example/todolistwithfirebase/
│   ├── MainActivity.kt                    # Activity principal
│   ├── presentation/
│   │   ├── auth/
│   │   │   ├── AuthViewModel.kt           # ViewModel de autenticação
│   │   │   ├── LoginScreen.kt             # Tela de login
│   │   │   └── SignUpScreen.kt            # Tela de cadastro
│   │   ├── tasks/
│   │   │   ├── TaskViewModel.kt           # ViewModel de tarefas
│   │   │   └── TaskListScreen.kt          # Tela de lista de tarefas
│   │   └── navigation/
│   │       ├── Routes.kt                  # Definição de rotas
│   │       └── NavGraph.kt                # Grafo de navegação
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Task.kt                    # Modelo Task
│   │   │   └── User.kt                    # Modelo User
│   │   └── repository/
│   │       ├── TaskRepository.kt          # Interface TaskRepository
│   │       └── AuthRepository.kt          # Interface AuthRepository
│   └── data/
│       └── repository/
│           ├── TaskRepositoryImpl.kt       # Implementação com Firestore
│           └── AuthRepositoryImpl.kt       # Implementação com Firebase Auth
└── build.gradle.kts                        # Dependências
```

---

## 📝 Aprendizados

### Kotlin Coroutines & Flows

- `callbackFlow`: Converter callbacks (Firebase listeners) em Flows
- `flatMapLatest`: Reagir automaticamente a mudanças de dependências
- `stateIn`: Manter listeners ativos enquanto ViewModel existe
- `StateFlow`: Estado cacheado e replayável

### Firebase

- `AuthStateListener`: Essencial para sincronizar estado de sessão
- Snapshot Listeners: Sincronização em tempo real
- Security Rules: Devem ser simples para snapshot listeners
- Filtragem: Fazer na query Android, não na rule

### Clean Architecture

- Separação de responsabilidades melhora testabilidade
- Repositories abstraem Firebase (facilitam mocking)
- Domain layer independente de framework

---

## 📄 Licença

Este projeto é fornecido como exemplo educacional.

---

## 👥 Autores

Desenvolvido como parte de projeto acadêmico/training em Android Development por Matheus Nascimento e Luccas Asaphe

---
