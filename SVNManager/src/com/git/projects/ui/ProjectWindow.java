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
 * @Description:   版本集成工具窗体入口          
 * 
 * @author 	lemon_mj 
 * @date 	2017年12月18日下午4:41:09
 */
public class ProjectWindow {

	private static final Logger logger = Logger.getLogger(ProjectWindow.class);
	
	private JFrame frame;
	
	private SvnProjectService svnProjectService = null;

	// svn操作用户
	private static User user;
	
	// 集成单编号
	private JTextField textField;
	
	// 集成单生成路径
	private JTextField textField_1;
	
	// 窗体操作信息显示
	private JLabel label_2 = new JLabel(" ");
	
	// 集成单输入窗体
	private JTextArea textArea = new JTextArea();
	
	// 提交注释
	private JTextField textField_2 = new JTextField();
	
	// 集成单路径
	private JLabel label_1 = new JLabel("集成单路径：");

	// 提交类型
	private JComboBox comboBox = new JComboBox();
	
	// 检出类型
	private JComboBox comboBox_1 = new JComboBox();
	
	// 源SVN
	private JComboBox comboBox_2 = new JComboBox();
	
	// 目标SVN
	private JComboBox comboBox_3 = new JComboBox();
	
	// SVN工程集合
	private List<Project> projectList;
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:41:50
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
	 * @date 	2017年12月18日下午4:46:05
	 * @Description:	Create the application.
	 */
	public ProjectWindow() {
		logger.info("Create the application...");
		logger.info("---------初始化svn配置信息......");
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
		logger.info("源SVN = " + sourceStr.toString());
		
		List<String> descList = svnDefine.getDescList();
		StringBuffer descStr = new StringBuffer("[");
		for(String desc : descList){
			comboBox_3.addItem(desc);
			descStr.append(desc + " ");
		}
		descStr.append("]");
		logger.info("目标SVN = " + descStr.toString());
		
		user = svnDefine.getUser();
		
		svnProjectService = new SvnProjectService();
		logger.info(user.toString());
		logger.info("---------初始化svn配置信息完成......");
		initialize();
	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:46:34
	 * @description:	获取SVN工程
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
	 * @date 	2017年12月18日 下午4:46:56
	 * @description:	资源库检出
	 *
	 * 
	 */
	private void checkOut(){
		// 检出目标
		String define = (String) comboBox_1.getSelectedItem();
		Project project = getProject(define);
		int cliResult = JOptionPane.showConfirmDialog(null, "确定要检出" + define + "库？","提示",JOptionPane.OK_CANCEL_OPTION);
		if(0 == cliResult){   // 确定
			logger.info("检出" + define + "库选择了确定按钮");
			long versionNum = svnProjectService.checkWorkCopy(project,user);
			if(versionNum != 0){
				logger.info(define + "库检出成功！版本号=" + versionNum + " check out 到目录：" + project.getWorkPath() + "中！");
				label_2.setText(define + "库检出成功！");
				label_1.setText("集成单路径：");
				JOptionPane.showMessageDialog(null, define + "库检出成功！");
			} else 
				logger.info("检出" + define + "库失败");
		} else
			logger.info("检出" + define + "库选择了取消关闭按钮");
	}
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:47:22
	 * @description:	SVN 0:提交 	1: 删除
	 *
	 * 
	 */
	public void commit(){
		// 源SVN
		String sourceDefine = (String) comboBox_2.getSelectedItem();
		Project source = getProject(sourceDefine);
		// 目标SVN
		String descDefine = (String) comboBox_3.getSelectedItem();
		Project desc = getProject(descDefine);
		if(sourceDefine.equals(descDefine)){
			JOptionPane.showMessageDialog(null, "源SVN，目标SVN资源库不能一致！请重新选择");
			return;
		}
		try{
			File file = new File(source.getWorkPath());
			if(!file.exists()){
				logger.info("源SVN[" + source.getWorkPath() + "]本地工作空间不存在，请检出" + sourceDefine + "资源库！");
				JOptionPane.showMessageDialog(null, "源SVN[" + source.getWorkPath() + "]本地工作空间不存在，请检出" + sourceDefine + "资源库！");
				return;
			}
			file = new File(desc.getWorkPath());
			if(!file.exists()){
				logger.info("目标SVN[" + desc.getWorkPath() + "]本地工作空间不存在，请检出" + descDefine + "资源库！");
				JOptionPane.showMessageDialog(null, "目标SVN[" + desc.getWorkPath() + "]本地工作空间不存在，请检出" + descDefine + "资源库！");
				return;
			}
			int cliResult = JOptionPane.showConfirmDialog(null, "确定要提交" + descDefine + "库集成单吗？","提示",JOptionPane.OK_CANCEL_OPTION);
			if(0 == cliResult){   // 确定
				logger.info("提交" + descDefine + "库选择了确定按钮");
				String filePath = textField_1.getText();
				if(filePath == null || filePath.equals("")){
					logger.info(descDefine + "库集成单路径为空！");
					JOptionPane.showMessageDialog(null, descDefine + "库集成单路径为空！");
					return;
				}
				// 集成单路径后缀
				String suffix = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
				if(!descDefine.toLowerCase().equals(suffix)){
					logger.info("集成单路径[" + filePath + "]跟目标[" + descDefine + "]SVN资源库不一至");
					JOptionPane.showMessageDialog(null, "请生成" + descDefine + "库集成单，再提交至SVN资源库！");
					return;
				}
				// 提交类型   0：提交   1：删除
				int submitType = comboBox.getSelectedIndex();
				if(submitType == 1){ 			// 删除操作
					int subResult = JOptionPane.showConfirmDialog(null, "确定要删除集成单对应的目标资源库文件吗？","提示",JOptionPane.OK_CANCEL_OPTION);						
					if(subResult != 0){
						logger.info("删除集成单对应的目标资源库文件选择了取消关闭按钮");
						return;
					}
					logger.info("删除集成单对应的目标资源库文件选择了确定按钮");
				} 					 // 提交
				user.setMessage("[" + textField_2.getText() + "]");
				Map<String,Object> map = ProjectUtils.makeAll(source, desc, user, filePath, svnProjectService,submitType);
				String resultCode = map.get("resultCode").toString();
				if("0".equals(resultCode)){
					String sucStr = submitType == 1 ? "删除完成！" : "提交完成！" ;
					label_2.setText(sucStr);
					JOptionPane.showMessageDialog(null, "集成单对应的" + descDefine  + "资源库文件" + sucStr);
				} else {
					String errmsg = map.get("errmsg").toString();
					String failStr = submitType == 1 ? "删除失败！" : "提交失败！" ;
					label_2.setText(failStr);
					JOptionPane.showMessageDialog(null, "集成单对应的" + descDefine  + "资源库文件" + failStr + errmsg);
				}
			} else 
				logger.info("提交" + descDefine + "库集成单选择了取消关闭按钮");
		} catch (Exception e) {
			logger.error(e);
			JOptionPane.showMessageDialog(null, "提交集成单发生异常！" + e.getMessage());
		}
	}
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:48:14
	 * @description:	生成目标资源库集成单
	 *
	 * @throws  Exception
	 */
	public void saveFile(){
		// 生成目标
		String define = (String) comboBox_3.getSelectedItem();
		Project project = getProject(define);
		int cliResult = JOptionPane.showConfirmDialog(null, "确定要生成" + define + "库集成单吗？","提示",JOptionPane.OK_CANCEL_OPTION);
		if(0 == cliResult){   // 确定
			logger.info("生成" + define + "库集成单选择了确定按钮");
			// 集成单编号
			String fileNum = textField.getText();
			// 集成单
			String content = textArea.getText();
			if(fileNum == null || fileNum.equals("")){
				logger.info("集成单编号为空！无法生成" + define + "库集成单");
				JOptionPane.showMessageDialog(null, "集成单编号为空！");
			} else {
				if(content == null || content.equals("")){
					logger.info("集成单为空！无法生成" + define + "库集成单");
					JOptionPane.showMessageDialog(null, "集成单为空！");
				} else {
					try {
						// 集成单路径
						String filePath = project.getFilePath() + fileNum + "." + define.toLowerCase();
						File file = new File(filePath);
						if(file.exists()){
							JOptionPane.showMessageDialog(null, "文件[" + filePath + "]已存在，重新录入集成单编号！");
							return;
						}
						FileUtils.createDetailList(filePath, content, "UTF-8");
						label_1.setText(define + "库集成单路径：");
						textField_1.setText(filePath);
						label_2.setText(define + "库集成单生成结束！");
					} catch (IOException e) {
						logger.error(e);
						JOptionPane.showMessageDialog(null, "生成集成单发生异常！" + e.getMessage());
					}
				}
			}
		} else
			logger.info("生成" + define + "库集成单选择了取消关闭按钮");
	}
	
	
	
	
	
	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:48:36
	 * @description:	Initialize the contents of the frame.
	 *
	 * @throws  Exception
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.getContentPane().setFont(new Font("宋体", Font.PLAIN, 12));
		
		frame.setTitle("版本集成工具");
		frame.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - 880) / 2, 
						(Toolkit.getDefaultToolkit().getScreenSize().height - 600) / 2, 880, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "集成单信息", TitledBorder.LEADING, TitledBorder.TOP, new Font("宋体", Font.PLAIN, 12), null));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "资源库检出", TitledBorder.LEADING, TitledBorder.TOP, new Font("宋体", Font.PLAIN, 12), null));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "资源库提交", TitledBorder.LEADING, TitledBorder.TOP, new Font("宋体", Font.PLAIN, 12), null));
		
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
		
		
		JLabel label = new JLabel("集成单编号：");
		label.setFont(new Font("宋体", Font.PLAIN, 12));
		
		textField = new JTextField();
		textField.setFont(new Font("宋体", Font.PLAIN, 12));
		textField.setColumns(10);
		
		label_1.setFont(new Font("宋体", Font.PLAIN, 12));
		
		textField_1 = new JTextField();
		textField_1.setFont(new Font("宋体", Font.PLAIN, 12));
		textField_1.setEditable(false);
		textField_1.setColumns(10);
		
		label_2.setForeground(Color.RED);
		label_2.setFont(new Font("宋体", Font.PLAIN, 12));
		
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
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		
		scrollPane.setViewportView(textArea);
		panel.setLayout(gl_panel);
		
		JLabel label_3 = new JLabel("检出目标：");
		label_3.setFont(new Font("宋体", Font.PLAIN, 12));
		
		
		comboBox_1.setFont(new Font("宋体", Font.PLAIN, 12));
		
		JButton button = new JButton("检出");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1)	// 鼠标左键点击
					checkOut();
			}
		});
		button.setFont(new Font("宋体", Font.PLAIN, 11));
		
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
		
		JLabel label_4 = new JLabel("源SVN：");
		label_4.setFont(new Font("宋体", Font.PLAIN, 12));
		
		
		comboBox_2.setFont(new Font("宋体", Font.PLAIN, 12));
		
		JLabel label_5 = new JLabel("目标SVN：");
		label_5.setFont(new Font("宋体", Font.PLAIN, 12));
		
		
		comboBox_3.setFont(new Font("宋体", Font.PLAIN, 12));
		
		JButton button_1 = new JButton("生成目标资源库集成单");
		button_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1)	// 鼠标左键点击
					saveFile();
			}
		});
		button_1.setFont(new Font("宋体", Font.PLAIN, 11));
		
		JLabel label_6 = new JLabel("提交类型：");
		label_6.setFont(new Font("宋体", Font.PLAIN, 12));
		
		
		comboBox.setFont(new Font("宋体", Font.PLAIN, 12));
		comboBox.addItem("提交");
		comboBox.addItem("删除");
		
		JLabel label_7 = new JLabel("提交备注：");
		label_7.setFont(new Font("宋体", Font.PLAIN, 12));
		
		
		textField_2.setColumns(10);
		
		JButton button_2 = new JButton("提交");
		button_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton() == MouseEvent.BUTTON1)	// 鼠标左键点击
					commit();
			}
		});
		button_2.setFont(new Font("宋体", Font.PLAIN, 11));
		
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
