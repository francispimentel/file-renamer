package br.com.francispimentel.filerenamer;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main extends JFrame {

	private static final long serialVersionUID = -8790035762434592998L;

	private FileRenamer fileRenamer;

	private JPanel contentPane;
	private JLabel labelSelecionePlanilha;
	private JTextField txtFieldPlanilha;
	private JButton btnSelecionarPlanilha;
	private JLabel labelTotalLinhasPlanilha;
	private JLabel labelSelecioneDiretorio;
	private JTextField textFieldRenomeados;
	private JButton btnSelecionarDiretorio;
	private JLabel labelTotalArquivosDiretorio;
	private JButton btnRenomear;
	private JLabel labelRenomeados;

	private Map<String, String> lines;
	private File inputDir;

	private boolean planilhaValida;
	private boolean diretorioValido;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Main frame = new Main();
				frame.setVisible(true);
			}
		});
	}

	public Main() {
		fileRenamer = new FileRenamer();

		setTitle("Renomeador De Arquivos");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 190);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		labelSelecionePlanilha = new JLabel("Selecione a planilha:");
		labelSelecionePlanilha.setFont(new Font("Tahoma", Font.BOLD, 14));
		labelSelecionePlanilha.setBounds(5, 5, 160, 20);
		contentPane.add(labelSelecionePlanilha);

		txtFieldPlanilha = new JTextField();
		txtFieldPlanilha.setEnabled(false);
		txtFieldPlanilha.setColumns(10);
		txtFieldPlanilha.setBounds(160, 5, 340, 20);
		contentPane.add(txtFieldPlanilha);

		btnSelecionarPlanilha = new JButton("Selecionar...");
		btnSelecionarPlanilha.setBounds(510, 5, 120, 20);
		btnSelecionarPlanilha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileFilter(new FileNameExtensionFilter("Spreadsheets (xls, xlsx)", "xls", "xlsx"));
				int returnVal = chooser.showOpenDialog(Main.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					txtFieldPlanilha.setText(chooser.getSelectedFile().getAbsolutePath());
					validarPlanilha(chooser.getSelectedFile());
				}
			}
		});
		contentPane.add(btnSelecionarPlanilha);

		labelTotalLinhasPlanilha = new JLabel();
		labelTotalLinhasPlanilha.setFont(new Font("Tahoma", Font.PLAIN, 12));
		labelTotalLinhasPlanilha.setBounds(160, 30, 340, 20);
		contentPane.add(labelTotalLinhasPlanilha);

		labelSelecioneDiretorio = new JLabel("Selecione o diretório:");
		labelSelecioneDiretorio.setFont(new Font("Tahoma", Font.BOLD, 14));
		labelSelecioneDiretorio.setBounds(5, 70, 160, 20);
		contentPane.add(labelSelecioneDiretorio);

		textFieldRenomeados = new JTextField();
		textFieldRenomeados.setEnabled(false);
		textFieldRenomeados.setColumns(10);
		textFieldRenomeados.setBounds(160, 70, 340, 20);
		contentPane.add(textFieldRenomeados);

		btnSelecionarDiretorio = new JButton("Selecionar...");
		btnSelecionarDiretorio.setBounds(510, 70, 120, 20);
		btnSelecionarDiretorio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showOpenDialog(Main.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					textFieldRenomeados.setText(chooser.getSelectedFile().getAbsolutePath());
					validarDiretorio(chooser.getSelectedFile());
				}
			}
		});
		contentPane.add(btnSelecionarDiretorio);

		labelTotalArquivosDiretorio = new JLabel();
		labelTotalArquivosDiretorio.setFont(new Font("Tahoma", Font.PLAIN, 12));
		labelTotalArquivosDiretorio.setBounds(160, 95, 340, 20);
		contentPane.add(labelTotalArquivosDiretorio);

		btnRenomear = new JButton("Renomear!");
		btnRenomear.setBounds(510, 135, 120, 20);
		btnRenomear.setEnabled(false);
		btnRenomear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int i = fileRenamer.renameFiles(inputDir, lines);
					labelRenomeados.setText(i + " arquivos renomeados com sucesso!");
				} catch (Exception e) {
					labelRenomeados.setText("Erro ao renomear: " + e.getMessage());
				}
				btnRenomear.setEnabled(false);
			}
		});
		contentPane.add(btnRenomear);

		labelRenomeados = new JLabel();
		labelRenomeados.setFont(new Font("Tahoma", Font.PLAIN, 12));
		labelRenomeados.setBounds(160, 135, 340, 20);
		contentPane.add(labelRenomeados);
	}

	private void validarPlanilha(File workbook) {
		if (workbook.exists()) {
			try {
				lines = fileRenamer.extractFilesNames(new FileInputStream(workbook));
				planilhaValida = !lines.isEmpty();
				labelTotalLinhasPlanilha.setText("Total de " + lines.size() + " linhas na planilha.");
			} catch (Exception e) {
				planilhaValida = false;
				labelTotalLinhasPlanilha.setText("Planilha inválida!");
			}
		} else {
			planilhaValida = false;
			labelTotalLinhasPlanilha.setText("O arquivo não existe!");
		}
		btnRenomear.setEnabled(planilhaValida && diretorioValido);
	}

	private void validarDiretorio(File dir) {
		diretorioValido = dir.exists() && dir.isDirectory() && dir.list().length > 0;
		this.inputDir = dir;
		if (!dir.exists()) {
			labelTotalArquivosDiretorio.setText("O diretório não existe!");
		} else if (!dir.isDirectory()) {
			labelTotalArquivosDiretorio.setText("Por favor selecione um diretório.");
		} else {
			labelTotalArquivosDiretorio.setText("Existem " + dir.list().length + " arquivos no diretório.");
		}
		btnRenomear.setEnabled(planilhaValida && diretorioValido);
	}
}
