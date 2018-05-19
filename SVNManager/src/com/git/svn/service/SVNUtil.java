package com.git.svn.service;

import java.io.File;  
  



import org.apache.log4j.Logger;  
import org.tmatesoft.svn.core.SVNCommitInfo;  
import org.tmatesoft.svn.core.SVNDepth;  
import org.tmatesoft.svn.core.SVNException;  
import org.tmatesoft.svn.core.SVNNodeKind;  
import org.tmatesoft.svn.core.SVNURL;  
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;  
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;  
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;  
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;  
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;  
import org.tmatesoft.svn.core.io.SVNRepository;  
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;  
import org.tmatesoft.svn.core.wc.SVNClientManager;  
import org.tmatesoft.svn.core.wc.SVNRevision;  
import org.tmatesoft.svn.core.wc.SVNStatus;  
import org.tmatesoft.svn.core.wc.SVNUpdateClient;  
import org.tmatesoft.svn.core.wc.SVNWCUtil;  
  
/**
 *  
 * @file	SVNUtil.java
 * @project	SVNManager
 * @Description:   SVNKit          
 * 
 * @author 	lemon_mj
 * @date 	2017��12��18������4:52:10
 */
public class SVNUtil {  
      
    private static Logger logger = Logger.getLogger(SVNUtil.class);  
      
    /**
     *   
     * @author 	lemon_mj
     * @date 	2017��12��18�� ����4:52:26
     * @description:	ͨ����ͬ��Э���ʼ���汾�� 
     *
     * 
     */
    public static void setupLibrary() {  
        DAVRepositoryFactory.setup();  
        SVNRepositoryFactoryImpl.setup();  
        FSRepositoryFactory.setup();  
    }  
  
    /** 
     *   
     * @author 	lemon_mj
     * @date 	2017��12��18�� ����4:52:40
     * @description:	��֤��¼svn 
     *
     * @param svnRoot
     * @param username
     * @param password
     * @return
     * 
     */
    @SuppressWarnings("deprecation")
	public static SVNClientManager authSvn(String svnRoot, String username, String password) {  
        // ��ʼ���汾��  
        setupLibrary();  
        // ����������  
        SVNRepository repository = null;  
        try {  
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnRoot));  
        } catch (SVNException e) {  
            logger.error(e);  
            return null;  
        }  
        // �����֤  
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);  
        // ���������֤������  
        repository.setAuthenticationManager(authManager);  
        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);  
        SVNClientManager clientManager = SVNClientManager.newInstance(options,authManager);  
        return clientManager;  
    }  
      
    /** 
     *  
     * @author 	lemon_mj
     * @date 	2017��12��18�� ����4:53:03
     * @description:	Make directory in svn repository 
     *
     * @param clientManager
     * @param url
     * @param commitMessage
     * @return
     * @throws  Exception
     */
    public static SVNCommitInfo makeDirectory(SVNClientManager clientManager,  
            SVNURL url, String commitMessage) {  
        try {  
            return clientManager.getCommitClient().doMkDir(new SVNURL[] { url }, commitMessage);  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
        return null;  
    }  
      
    /** 
     *   
     * @author 	lemon_mj
     * @date 	2017��12��18�� ����4:53:14
     * @description:	Imports an unversioned directory into a repository location denoted by a destination URL
     *
     * @param clientManager
     * @param localPath
     * @param dstURL
     * @param commitMessage
     * @param isRecursive
     * @return
     * @throws  Exception
     */
    public static SVNCommitInfo importDirectory(SVNClientManager clientManager,  
            File localPath, SVNURL dstURL, String commitMessage,  
            boolean isRecursive) {  
        try {  
            return clientManager.getCommitClient().doImport(localPath, dstURL,  
                    commitMessage, null, true, true,  
                    SVNDepth.fromRecurse(isRecursive));  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
        return null;  
    }  
      
    /** 
     * Puts directories and files under version control 
     * @param clientManager 
     *          SVNClientManager 
     * @param wcPath  
     *          work copy path 
     */  
    public static void addEntry(SVNClientManager clientManager, File wcPath) {  
        try {  
            clientManager.getWCClient().doAdd(new File[] { wcPath }, true,  
                    false, false, SVNDepth.INFINITY, false, false, true);  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
    }  
        
    
    public static void delEntry(SVNClientManager clientManager, File wcPath){
    	try {
			clientManager.getWCClient().doDelete(wcPath, true, false);
		} catch (SVNException e) {
			logger.error( e);  
		}
    }
    
    /** 
     * Collects status information on a single Working Copy item 
     * @param clientManager 
     * @param wcPath 
     *          local item's path 
     * @param remote 
     *          true to check up the status of the item in the repository,  
     *          that will tell if the local item is out-of-date (like '-u' option in the SVN client's  
     *          'svn status' command), otherwise false 
     * @return 
     * @throws SVNException 
     */  
    public static SVNStatus showStatus(SVNClientManager clientManager,  
            File wcPath, boolean remote) {  
        SVNStatus status = null;  
        try {  
            status = clientManager.getStatusClient().doStatus(wcPath, remote);  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
        return status;  
    }  
      
    /** 
     * Commit work copy's change to svn 
     * @param clientManager 
     * @param wcPath  
     *          working copy paths which changes are to be committed 
     * @param keepLocks 
     *          whether to unlock or not files in the repository 
     * @param commitMessage 
     *          commit log message 
     * @return 
     * @throws SVNException 
     */  
    public static SVNCommitInfo commit(SVNClientManager clientManager,  
            File wcPath, boolean keepLocks, String commitMessage) {  
        try {  
            return clientManager.getCommitClient().doCommit(  
                    new File[] { wcPath }, keepLocks, commitMessage, null,  
                    null, false, false, SVNDepth.INFINITY);  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
        return null;  
    }  
      
    /** 
     * Updates a working copy (brings changes from the repository into the working copy). 
     * @param clientManager 
     * @param wcPath 
     *          working copy path 
     * @param updateToRevision 
     *          revision to update to 
     * @param depth 
     *          update����ȣ�Ŀ¼����Ŀ¼���ļ� 
     * @return 
     * @throws SVNException 
     */  
    public static long update(SVNClientManager clientManager, File wcPath,  
            SVNRevision updateToRevision, SVNDepth depth) {  
        try {  
        	SVNUpdateClient updateClient = clientManager.getUpdateClient();  
        	/* 
        	 * sets externals not to be ignored during the update 
        	 */  
        	updateClient.setIgnoreExternals(false);  
        	/* 
        	 * returns the number of the revision wcPath was updated to 
        	 */  
            return updateClient.doUpdate(wcPath, updateToRevision,depth, false, false);  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
        return 0;  
    }  
      
    /** 
     * recursively checks out a working copy from url into wcDir 
     * @param clientManager 
     * @param url 
     *          a repository location from where a Working Copy will be checked out 
     * @param revision 
     *          the desired revision of the Working Copy to be checked out 
     * @param destPath 
     *          the local path where the Working Copy will be placed 
     * @param depth 
     *          checkout����ȣ�Ŀ¼����Ŀ¼���ļ� 
     * @return 
     * @throws SVNException 
     */  
    public static long checkout(SVNClientManager clientManager, SVNURL url,  
            SVNRevision revision, File destPath, SVNDepth depth) {  
        try {  
        	SVNUpdateClient updateClient = clientManager.getUpdateClient();  
        	/* 
        	 * sets externals not to be ignored during the checkout 
        	 */  
        	updateClient.setIgnoreExternals(false);  
        	/* 
        	 * returns the number of the revision at which the working copy is 
        	 */  
            return updateClient.doCheckout(url, destPath, revision, revision,depth, false);  
        } catch (SVNException e) {  
            logger.error(e);  
        }  
        return 0;  
    }  
      
    /** 
     * ȷ��path�Ƿ���һ�������ռ� 
     * @param path 
     * @return 
     */  
    public static boolean isWorkingCopy(File path){  
        if(!path.exists()){  
            return false;  
        }  
        try {  
            if(null == SVNWCUtil.getWorkingCopyRoot(path, false))
                return false;  
        } catch (SVNException e) {  
            logger.error(e);  
            return false; 
        }  
        return true;  
    }  
      
    /** 
     * ȷ��һ��URL��SVN���Ƿ���� 
     * @param url 
     * @return 
     */  
	@SuppressWarnings("deprecation")
	public static boolean isURLExist(SVNURL url,String username,String password){  
        try {  
        	SVNRepository svnRepository = SVNRepositoryFactory.create(url);  
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);  
            svnRepository.setAuthenticationManager(authManager);  
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1);  
            return nodeKind == SVNNodeKind.NONE ? false : true;   
        } catch (SVNException e) {  
            logger.error(e);
        }  
        return false;  
    }  
  
}  