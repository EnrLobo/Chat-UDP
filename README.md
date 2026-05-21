# Chat UDP - Java Swing

Projeto desenvolvido como atividade da disciplina de Sistemas Computacionais Distribuídos do curso de Bacharelado em Sistemas de Informação (IFSULDEMINAS - Campus Machado).

Trata-se de uma aplicação de chat peer-to-peer baseada na troca de mensagens em rede utilizando sockets UDP. O repositório contém a implementação da API base de comunicação e uma interface gráfica customizada desenvolvida em Java Swing.

## Funcionalidades

* **Comunicação UDP:** Envio e recebimento assíncrono de pacotes de dados através da rede.
* **Interface Gráfica Customizada:** Interface limpa e estilizada com painel de configurações estruturado e área de mensagens renderizada em formato de balões.
* **Parametrização em Tempo de Execução:** Definição de Porta Local, IP Remoto, Porta Remota e Nome de Usuário diretamente pela interface do sistema.
* **Tratamento de Dados:** Limpeza de buffers para evitar a exibição de caracteres nulos (fantasmas) na leitura dos pacotes recebidos.

## Pré-requisitos

Para executar este projeto, você precisará ter instalado em sua máquina:
* Java Development Kit (JDK)
* IntelliJ IDEA (ou outra IDE de sua preferência com suporte a projetos Java)

## Como executar o projeto

1. Faça o clone deste repositório para a sua máquina local.
2. Abra o diretório do projeto no IntelliJ IDEA.
3. Localize o arquivo `ChatGUI.java` (localizado no pacote `br.edu.ifsuldeminas.sd.chat.client`).
4. Para testar a comunicação localmente, você precisará rodar duas instâncias da aplicação simultaneamente. No IntelliJ, vá até **Edit Configurations...**, selecione a classe `ChatGUI`, clique em **Modify options** e marque a opção **Allow multiple instances** (ou **Allow parallel run**).
5. Execute o método `main` da classe `ChatGUI` duas vezes para abrir duas janelas.

### Configurando a Conexão

Na **Instância A** (Primeira Janela):
* Porta Local: 8000
* IP Remoto: localhost
* Porta Remota: 8001
* Seu Nome: (Defina um nome)
* Clique em Conectar.

Na **Instância B** (Segunda Janela):
* Porta Local: 8001
* IP Remoto: localhost
* Porta Remota: 8000
* Seu Nome: (Defina outro nome)
* Clique em Conectar.

Após ambas as instâncias estarem conectadas, você poderá enviar mensagens entre elas utilizando o campo inferior e o botão de envio (ou a tecla Enter).

## Estrutura do Código

A arquitetura do projeto respeita o encapsulamento da API original de comunicação:

* `Sender` / `Receiver`: Interfaces que definem os contratos de envio e recebimento de mensagens.
* `UDPSender` / `UDPReceiver`: Implementações concretas das interfaces utilizando `DatagramSocket` e `DatagramPacket`.
* `ChatFactory`: Fábrica responsável por instanciar a comunicação e ocultar a infraestrutura UDP da camada de visualização.
* `MessageContainer`: Interface obrigatória para a classe de visualização, determinando onde e como as mensagens serão apresentadas.
* `ChatGUI`: Implementação em Swing que atua como cliente consumindo a API do chat e implementando o `MessageContainer`.

## Créditos

A API de base de comunicação UDP (`Sender`, `Receiver`, `ChatFactory`, etc.) foi fornecida como material de estudo pelo Prof. Dr. Emerson Assis de Carvalho. A implementação visual e os ajustes de buffer foram desenvolvidos como extensão da atividade proposta.
