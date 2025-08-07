package com.example.gerenciador.view;


import com.example.gerenciador.dao.UsuarioDAO;
import com.example.gerenciador.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class MainView extends JFrame {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Email"}, 0);
    private final JTable tabelaUsuarios = new JTable(tableModel);
    private final JTextField campoNome = new JTextField(20);
    private final JTextField campoEmail = new JTextField(20);
    private final JTextField campoId = new JTextField(5);

    // Construtor da janela
    public MainView() {
        super("Gerenciador de Usuários - Swing");
        configurarJanela();
        criarComponentes();
        carregarDadosNaTabela();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setLayout(new BorderLayout(10, 10));
    }

    private void criarComponentes() {
        // Painel para os campos de entrada e botões
        JPanel painelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelSuperior.add(new JLabel("Nome:"));
        painelSuperior.add(campoNome);
        painelSuperior.add(new JLabel("Email:"));
        painelSuperior.add(campoEmail);

        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarUsuario());
        painelSuperior.add(btnAdicionar);

        // Painel para a tabela
        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Painel para as operações de busca, atualização e deleção
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        painelInferior.add(new JLabel("ID:"));
        painelInferior.add(campoId);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarUsuarioPorId());
        painelInferior.add(btnBuscar);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarUsuario());
        painelInferior.add(btnAtualizar);

        JButton btnDeletar = new JButton("Deletar");
        btnDeletar.addActionListener(e -> deletarUsuario());
        painelInferior.add(btnDeletar);

        JButton btnLimparCampos = new JButton("Limpar");
        btnLimparCampos.addActionListener(e -> limparCampos());
        painelInferior.add(btnLimparCampos);

        add(painelSuperior, BorderLayout.NORTH);
        add(painelInferior, BorderLayout.SOUTH);
    }

    private void carregarDadosNaTabela() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        for (Usuario u : usuarios) {
            tableModel.addRow(new Object[]{u.getId(), u.getNome(), u.getEmail()});
        }
    }

    private void adicionarUsuario() {
        String nome = campoNome.getText();
        String email = campoEmail.getText();

        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Email não podem estar vazios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario novoUsuario = new Usuario(nome, email);
        usuarioDAO.adicionar(novoUsuario);
        carregarDadosNaTabela();
        limparCampos();
    }

    private void buscarUsuarioPorId() {
        try {
            int id = Integer.parseInt(campoId.getText());
            Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorId(id);
            if (usuarioOpt.isPresent()) {
                Usuario u = usuarioOpt.get();
                JOptionPane.showMessageDialog(this, "Usuário encontrado:\n" + u.toString(), "Busca de Usuário", JOptionPane.INFORMATION_MESSAGE);
                // Preencher campos com dados para possível atualização
                campoNome.setText(u.getNome());
                campoEmail.setText(u.getEmail());
            } else {
                JOptionPane.showMessageDialog(this, "Usuário com ID " + id + " não encontrado.", "Busca de Usuário", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID numérico válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarUsuario() {
        try {
            int id = Integer.parseInt(campoId.getText());
            String novoNome = campoNome.getText();
            String novoEmail = campoEmail.getText();

            if (novoNome.isEmpty() || novoEmail.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e Email não podem estar vazios para a atualização.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario usuario = new Usuario(novoNome, novoEmail);
            usuario.setId(id);

            usuarioDAO.atualizar(usuario);
            carregarDadosNaTabela();
            limparCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID numérico válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarUsuario() {
        try {
            int id = Integer.parseInt(campoId.getText());
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar o usuário com ID " + id + "?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                usuarioDAO.deletar(id);
                carregarDadosNaTabela();
                limparCampos();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID numérico válido.", "Erro de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        campoNome.setText("");
        campoEmail.setText("");
        campoId.setText("");
    }

    public static void main(String[] args) {
        // Garantir que a GUI seja criada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            view.setVisible(true);
        });
    }
}