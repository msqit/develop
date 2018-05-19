package com.git.projects.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import com.git.svn.bean.Project;
import com.git.svn.bean.SVNDefine;
import com.git.svn.bean.User;
import com.git.svn.service.SvnProjectService;
import com.git.svn.utils.ConfigManager;
import com.git.svn.utils.FileUtils;
import com.git.svn.utils.ProjectUtils;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 
 * @file	ProjectWindow.java
 * @project	SVNManager
 * @Description:   �汾���ɹ��ߴ������          
 * 
 * @author 	lemon_mj 
 * @date 	2017��12��18������4:41:09
 */
public class ProjectWindow {

	private static final Logger logger = Logger.getLogger(ProjectWindow.class);
	
	private JFrame frame;
	
	private SvnProjectService svnProjectService = null;

	// svn�����û�
	private static User user;
	
	// ���ɵ����
	private JTextField textField;
	
	// ���ɵ�����·��
	private JTextField textField_1;
	
	// ���������Ϣ��ʾ
	private JLabel label_2 = new JLabel(" ");
	
	// ���ɵ����봰��
	private JTextArea textArea = new JTextArea();
	
	// �ύע��
	private JTextField textField_2 = new JTextField();
	
	// ���ɵ�·��
	private JLabel label_1 = new JLabel("���ɵ�·����");

	// �ύ����
	private JComboBox comboBox = new JComboBox();
	
	// �������
	private JComboBox comboBox_1 = new JComboBox();
	
	// ԴSVN
	private JComboBox comboBox_2 = new JComboBox();
	
	// Ŀ��SVN
	private JComboBox comboBox_3 = new JComboBox();
	
	// SVN���̼���
	private List<Project> projectList;
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017��12��18�� ����4:41:50
	 * @description:	Launch the application.
	 *
	 * @param args
	 * 
	 */
	public static void main(String[] args) {
		logger.info("Launch the application.");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProjectWindow window = new ProjectWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					logger.error(e);				
				}
			}
		});
	}

	/**
	 * 
	 * @author	lemon_mj 
	 * @date 	2017��12��18������4:46:05
	 * @Description:	Create the application.
	 */
	public ProjectWindow() {
		logger.info("Create the application...");
		logger.info("---------��ʼ��svn������Ϣ......");
		SVNDefine svnDefine = ConfigManager.getSVNDefine();
		
		
		projectList = svnDefine.getProjectList();
		for(Project project : projectList){
			comboBox_1.addItem(project.getDefine());
			logger.info(project.toString());
		}
		
		List<String> sourceList = svnDefine.getSourceList();
		StringBuffer sourceStr = new StringBuffer("[");
		for(String source : sourceList){
			comboBox_2.addItem(source);
			sourceStr.append(source + " ");
		}
		sourceStr.append("]");
		logger.info("ԴSVN = " + sourceStr.toString());
		
		List<String> descList = svnDefine.getDescList();
		StringBuffer descStr = new StringBuffer("[");
		for(String desc : descList){
			comboBox_3.addItem(desc);
			descStr.append(desc + " ");
		}
		descStr.append("]");
		logger.info("Ŀ��SVN = " + descStr.toString());
		
		user = svnDefine.getUser();
		
		svnProjectService = new SvnProjectService();
		logger.info(user.toString());
		logger.info("---------��ʼ��svn������Ϣ���......");
		initialize();
	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017��12��18�� ����4:46:34
	 * @description:	��ȡSVN����
	 *
	 * @param define
	 * @return
	 * 
	 */
	private Project getProject(String define){
		Project project = null;
		for(Project p : projectList){
			if(define.equals(p.getDefine())){
				project = p;
				break;
			}
		}
		return project;
	}
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017��12��18�� ����4:46:56
	 * @description:	��Դ����
	 *
	 * 
	 */
	private void checkOut(){
		// ���Ŀ��
		String define = (String) comboBox_1.getSelectedItem();
		Project project = getProject(define);
		int cliResult = JOptionPane.showConfirmDialog(null, "ȷ��Ҫ���" + define + "�⣿","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if(0 == cliResult){   // ȷ��
			logger.info("���" + define + "��ѡ����ȷ����ť");
			long versionNum = svnProjectService.checkWorkCopy(project,user);
			if(versionNum != 0){
				logger.info(define + "�����ɹ����汾��=" + versionNum + " check out ��Ŀ¼��" + project.getWorkPath() + "�У�");
				label_2.setText(define + "�����ɹ���");
				label_1.setText("���ɵ�·����");
				JOptionPane.showMessageDialog(null, define + "�����ɹ���");
			} else 
				logger.info("���" + define + "��ʧ��");
		} else
			logger.info("���" + define + "��ѡ����ȡ���رհ�ť");
	}
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017��12��18�� ����4:47:22
	 * @description:	SVN 0:�ύ 	1: ɾ��
	 *
	 * 
	 */
	public void commit(){
		// ԴSVN
		String sourceDefine = (String) comboBox_2.getSelectedItem();
		Project source = getProject(sourceDefine);
		// Ŀ��SVN
		String descDefine = (String) comboBox_3.getSelectedItem();
		Project desc = getProject(descDefine);
		if(sourceDefine.equals(descDefine)){
			JOptionPane.showMessageDialog(null, "ԴSVN��Ŀ��SVN��Դ�ⲻ��һ�£�������ѡ��");
			return;
		}
		try{
			File file = new File(source.getWorkPath());
			if(!file.exists()){
				logger.info("ԴSVN[" + source.getWorkPath() + "]���ع����ռ䲻���ڣ�����" + sourceDefine + "��Դ�⣡");
				JOptionPane.showMessageDialog(null, "ԴSVN[" + source.getWorkPath() + "]���ع����ռ䲻���ڣ�����" + sourceDefine + "��Դ�⣡");
				return;
			}
			file = new File(desc.getWorkPath());
			if(!file.exists()){
				logger.info("Ŀ��SVN[" + desc.getWorkPath() + "]���ع����ռ䲻���ڣ�����" + descDefine + "��Դ�⣡");
				JOptionPane.showMessageDialog(null, "Ŀ��SVN[" + desc.getWorkPath() + "]���ع����ռ䲻���ڣ�����" + descDefine + "��Դ�⣡");
				return;
			}
			int cliResult = JOptionPane.showConfirmDialog(null, "ȷ��Ҫ�ύ" + descDefine + "�⼯�ɵ���","��ʾ",JOptionPane.OK_CANCEL_OPTION);
			if(0 == cliResult){   // ȷ��
				logger.info("�ύ" + descDefine + "��ѡ����ȷ����ť");
				String filePath = textField_1.getText();
				if(filePath == null || filePath.equals("")){
					logger.info(descDefine + "�⼯�ɵ�·��Ϊ�գ�");
					JOptionPane.showMessageDialog(null, descDefine + "�⼯�ɵ�·��Ϊ�գ�");
					return;
				}
				// ���ɵ�·����׺
				String suffix = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
				if(!descDefine.toLowerCase().equals(suffix)){
					logger.info("���ɵ�·��[" + filePath + "]��Ŀ��[" + descDefine + "]SVN��Դ�ⲻһ��");
					JOptionPane.showMessageDialog(null, "������" + descDefine + "�⼯�ɵ������ύ��SVN��Դ�⣡");
					return;
				}
				// �ύ����   0���ύ   1��ɾ��
				int submitType = comboBox.getSelectedIndex();
				if(submitType == 1){ 			// ɾ������
					int subResult = JOptionPane.showConfirmDialog(null, "ȷ��Ҫɾ�����ɵ���Ӧ��Ŀ����Դ���ļ���","��ʾ",JOptionPane.OK_CANCEL_OPTION);						
					if(subResult != 0){
						logger.info("ɾ�����ɵ���Ӧ��Ŀ����Դ���ļ�ѡ����ȡ���رհ�ť");
						return;
					}
					logger.info("ɾ�����ɵ���Ӧ��Ŀ����Դ���ļ�ѡ����ȷ����ť");
				} 					 // �ύ
				user.setMessage("[" + textField_2.getText() + "]");
				Map<String,Object> map = ProjectUtils.makeAll(source, desc, user, filePath, svnProjectService,submitType);
				String resultCode = map.get("resultCode").toString();
				if("0".equals(resultCode)){
					String sucStr = submitType == 1 ? "ɾ����ɣ�" : "�ύ��ɣ�" ;
					label_2.setText(sucStr);
					JOptionPane.showMessageDialog(null, "���ɵ���Ӧ��" + descDefine  + "��Դ���ļ�" + sucStr);
				} else {
					String errmsg = map.get("errmsg").toString();
					String failStr = submitType == 1 ? "ɾ��ʧ�ܣ�" : "�ύʧ�ܣ�" ;
					label_2.setText(failStr);
					JOptionPane.showMessageDialog(null, "���ɵ���Ӧ��" + descDefine  + "��Դ���ļ�" + failStr + errmsg);
				}
			} else 
				logger.info("�ύ" + descDefine + "�⼯�ɵ�ѡ����ȡ���رհ�ť");
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "�ύ���ɵ������쳣��" + e.getMessage());
		}
	}
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017��12��18�� ����4:48:14
	 * @description:	����Ŀ����Դ�⼯�ɵ�
	 *
	 * @throws  Exception
	 */
	public void saveFile(){
		// ����Ŀ��
		String define = (String) comboBox_3.getSelectedItem();
		Project project = getProject(define);
		int cliResult = JOptionPane.showConfirmDialog(null, "ȷ��Ҫ����" + define + "�⼯�ɵ���","��ʾ",JOptionPane.OK_CANCEL_OPTION);
		if(0 == cliResult){   // ȷ��
			logger.info("����" + define + "�⼯�ɵ�ѡ����ȷ����ť");
			// ���ɵ����
			String fileNum = textField.getText();
			// ���ɵ�
			String content = textArea.getText();
			if(fileNum == null || fileNum.equals("")){
				logger.info("���ɵ����Ϊ�գ��޷�����" + define + "�⼯�ɵ�");
				JOptionPane.showMessageDialog(null, "���ɵ����Ϊ�գ�");
			} else {
				if(content == null || content.equals("")){
					logger.info("���ɵ�Ϊ�գ��޷�����" + define + "�⼯�ɵ�");
					JOptionPane.showMessageDialog(null, "���ɵ�Ϊ�գ�");
				} else {
					try {
						// ���ɵ�·��
						String filePath = project.getFilePath() + fileNum + "." + define.toLowerCase();
						File file = new File(filePath);
						if(file.exists()){
							JOptionPane.showMessageDialog(null, "�ļ�[" + filePath + "]�Ѵ��ڣ�����¼�뼯�ɵ���ţ�");
							return;
						}
						FileUtils.createDetailList(filePath, content, "UTF-8");
						label_1.setText(define + "�⼯�ɵ�·����");
						textField_1.setText(filePath);
						label_2.setText(define + "�⼯�ɵ����ɽ�����");
					} catch (IOException e) {
						logger.error(e);
						JOptionPane.showMessageDialog(null, "���ɼ��ɵ������쳣��" + e.getMessage());
					}
				}
			}
		} else
			logger.info("����" + define + "�⼯�ɵ�ѡ����ȡ���رհ�ť");
	}
	
	
	
	
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017��12��18�� ����4:48:36
	 * @description:	Initialize the contents of the frame.
	 *
	 * @throws  Exception
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setFont(new Font("����", Font.PLAIN, 12));
		
		frame.setTitle("�汾���ɹ���");
		frame.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - 880) / 2, 
						(Toolkit.getDefaultToolkit().getScreenSize().height - 600) / 2, 880, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "���ɵ���Ϣ", TitledBorder.LEADING, TitledBorder.TOP, new Font("����", Font.PLAIN, 12), null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "��Դ����", TitledBorder.LEADING, TitledBorder.TOP, new Font("����", Font.PLAIN, 12), null));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "��Դ���ύ", TitledBorder.LEADING, TitledBorder.TOP, new Font("����", Font.PLAIN, 12), null));
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(19)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(panel_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE))
					.addContainerGap(20, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 333, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		
		JLabel label = new JLabel("���ɵ���ţ�");
		label.setFont(new Font("����", Font.PLAIN, 12));
		
		textField = new JTextField();
		textField.setFont(new Font("����", Font.PLAIN, 12));
		textField.setColumns(10);
		
		label_1.setFont(new Font("����", Font.PLAIN, 12));
		
		textField_1 = new JTextField();
		textField_1.setFont(new Font("����", Font.PLAIN, 12));
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		
		label_2.setForeground(Color.RED);
		label_2.setFont(new Font("����", Font.PLAIN, 12));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(65)
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
							.addGap(40)
							.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 392, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(208)
							.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_1)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(label_2)
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		textArea.setLineWrap(true);
		textArea.setFont(new Font("����", Font.PLAIN, 12));
		
		scrollPane.setViewportView(textArea);
		panel.setLayout(gl_panel);
		
		JLabel label_3 = new JLabel("���Ŀ�꣺");
		label_3.setFont(new Font("����", Font.PLAIN, 12));
		
		
		comboBox_1.setFont(new Font("����", Font.PLAIN, 12));
		
		JButton button = new JButton("���");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1)	// ���������
					checkOut();
			}
		});
		button.setFont(new Font("����", Font.PLAIN, 11));
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap(242, Short.MAX_VALUE)
					.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
					.addGap(82)
					.addComponent(button, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
					.addGap(216))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_3)
						.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		
		JLabel label_4 = new JLabel("ԴSVN��");
		label_4.setFont(new Font("����", Font.PLAIN, 12));
		
		
		comboBox_2.setFont(new Font("����", Font.PLAIN, 12));
		
		JLabel label_5 = new JLabel("Ŀ��SVN��");
		label_5.setFont(new Font("����", Font.PLAIN, 12));
		
		
		comboBox_3.setFont(new Font("����", Font.PLAIN, 12));
		
		JButton button_1 = new JButton("����Ŀ����Դ�⼯�ɵ�");
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1)	// ���������
					saveFile();
			}
		});
		button_1.setFont(new Font("����", Font.PLAIN, 11));
		
		JLabel label_6 = new JLabel("�ύ���ͣ�");
		label_6.setFont(new Font("����", Font.PLAIN, 12));
		
		
		comboBox.setFont(new Font("����", Font.PLAIN, 12));
		comboBox.addItem("�ύ");
		comboBox.addItem("ɾ��");
		
		JLabel label_7 = new JLabel("�ύ��ע��");
		label_7.setFont(new Font("����", Font.PLAIN, 12));
		
		
		textField_2.setColumns(10);
		
		JButton button_2 = new JButton("�ύ");
		button_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1)	// ���������
					commit();
			}
		});
		button_2.setFont(new Font("����", Font.PLAIN, 11));
		
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(40)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(label_6, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(comboBox_2, 0, 74, Short.MAX_VALUE))
					.addGap(56)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(label_5, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_7, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE))
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGap(19)
							.addComponent(comboBox_3, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
							.addGap(84)
							.addComponent(button_1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel_2.createSequentialGroup()
							.addGap(18)
							.addComponent(textField_2)))
					.addGap(32)
					.addComponent(button_2, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
					.addGap(20))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_4)
						.addComponent(comboBox_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_1)
						.addComponent(comboBox_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_5))
					.addGap(21)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(label_6)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_7)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button_2))
					.addContainerGap(28, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		
		frame.getContentPane().setLayout(groupLayout);
	}
	
	
}
