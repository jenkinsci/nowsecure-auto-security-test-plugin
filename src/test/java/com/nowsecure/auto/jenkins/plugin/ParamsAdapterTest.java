package com.nowsecure.auto.jenkins.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.nowsecure.auto.domain.NSAutoParameters;
import com.nowsecure.auto.domain.ProxySettings;
import com.nowsecure.auto.utils.IOHelper;

import hudson.AbortException;

public class ParamsAdapterTest implements NSAutoParameters {
	private static final String TEST_IPA_NAME = "test.ipa";
	private static final String TEST_APK_NAME = "test.apk";
    
    private String url = "https://lab-api.nowsecure.com";
    private String token = "token";
    private File workspace;
    private File artifactsDir;
    private File file;
    private File ipaFile;
    private File tmpDir;
    private String username;
    private String password;
    private boolean showStatusMessages;
    private String stopTestsForStatusMessage;
    private boolean debug;

    private int score;
    private int minutes;
    final IOHelper helper = new IOHelper("name", 0);

    @Before
    public void setup() throws IOException {
    	tmpDir = Files.createTempDir();
    	tmpDir.deleteOnExit();
    	artifactsDir = tmpDir;
    	workspace = tmpDir;
        ipaFile = new File(tmpDir.getAbsolutePath() + "/" + TEST_IPA_NAME);
        try (FileWriter writer = new FileWriter(ipaFile);) {
        	//cannot be zero length or verify of file may be false
        	writer.write("Hello world!");
        }
        ipaFile.deleteOnExit();
        file = new File(workspace.getAbsolutePath() + "/" + TEST_APK_NAME);
        try (FileWriter writer = new FileWriter(file);) {
        	//cannot be zero length or verify of file may be false
        	writer.write("Hello world!");
        }
        file.deleteOnExit();

    }
    
    @After
    public void tearDown() {
    	ipaFile.delete();
    	file.delete();
    	tmpDir.delete();
    	artifactsDir.delete();
    	tmpDir.delete();
    }
    
    @Test
    public void testConstructor() throws Exception {
        ParamsAdapter param = new ParamsAdapter(this, "newToken", tmpDir, tmpDir, ipaFile.getAbsolutePath(), true, true, "pluginName", "bill",
                "pass", true, "stop", new ProxySettings(), true);
        Assert.assertEquals("newToken", param.getApiKey());
        Assert.assertNotNull(param.getApiUrl());
        Assert.assertEquals("desc", param.getDescription());
        Assert.assertEquals("group", param.getGroup());
        Assert.assertEquals(tmpDir, param.getArtifactsDir());
        Assert.assertEquals(ipaFile, param.getFile());
        Assert.assertEquals(30, param.getWaitMinutes());
        Assert.assertEquals(70, param.getScoreThreshold());
        Assert.assertEquals("pass", param.getPassword());
        Assert.assertEquals("stop", param.getStopTestsForStatusMessage());
        Assert.assertEquals("bill", param.getUsername());
        Assert.assertTrue(param.isShowStatusMessages());
    }

    @Test
    public void testConstructorWithScore() throws Exception {
        ParamsAdapter param = new ParamsAdapter(this, "newToken", workspace, tmpDir, ipaFile.getAbsolutePath(), true, true, "pluginName",
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), false);

        Assert.assertEquals("newToken", param.getApiKey());
        Assert.assertNotNull(param.getApiUrl());
        Assert.assertEquals("desc", param.getDescription());
        Assert.assertEquals("group", param.getGroup());
        Assert.assertEquals(tmpDir, param.getArtifactsDir());
        Assert.assertEquals(ipaFile, param.getFile());
        score = 60;
        minutes = 40;
        Assert.assertEquals(40, param.getWaitMinutes());
        Assert.assertEquals(60, param.getScoreThreshold());
    }

    @Test
    public void testConstructorWait() throws Exception {
        ParamsAdapter param = new ParamsAdapter(this, "newToken", workspace, tmpDir, ipaFile.getAbsolutePath(), false, true, "pluginName",
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), true);
        Assert.assertEquals("newToken", param.getApiKey());
        Assert.assertNotNull(param.getApiUrl());
        Assert.assertEquals("desc", param.getDescription());
        Assert.assertEquals("group", param.getGroup());
        Assert.assertEquals(tmpDir, param.getArtifactsDir());
        Assert.assertEquals(ipaFile, param.getFile());
        Assert.assertEquals(30, param.getWaitMinutes());
        Assert.assertEquals(0, param.getScoreThreshold());
    }

    @Test
    public void testConstructorScore() throws Exception {
        ParamsAdapter param = new ParamsAdapter(this, "newToken", workspace, tmpDir, ipaFile.getAbsolutePath(), true, false, "pluginName",
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), false);
        Assert.assertEquals("newToken", param.getApiKey());
        Assert.assertNotNull(param.getApiUrl());
        Assert.assertEquals("desc", param.getDescription());
        Assert.assertEquals("group", param.getGroup());
        Assert.assertEquals(tmpDir, param.getArtifactsDir());
        Assert.assertEquals(ipaFile, param.getFile());
        Assert.assertEquals(0, param.getWaitMinutes());
        Assert.assertEquals(0, param.getScoreThreshold());
    }

    @Test
    public void testConstructorNoWait() throws Exception {
        ParamsAdapter param = new ParamsAdapter(this, "newToken", workspace, tmpDir, ipaFile.getAbsolutePath(), false, false, "pluginName",
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), true);
        Assert.assertEquals("newToken", param.getApiKey());
        Assert.assertNotNull(param.getApiUrl());
        Assert.assertEquals("desc", param.getDescription());
        Assert.assertEquals("group", param.getGroup());
        Assert.assertEquals(tmpDir, param.getArtifactsDir());
        Assert.assertEquals(ipaFile, param.getFile());
        Assert.assertEquals(0, param.getWaitMinutes());
        Assert.assertEquals(0, param.getScoreThreshold());
    }

    @Test(expected = AbortException.class)
    public void testConstructorNullToken() throws Exception {
        token = null;
        new ParamsAdapter(this, null, tmpDir, ipaFile, "binary ", true, true, null,
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), true);
    }

    @Test(expected = AbortException.class)
    public void testConstructorBinary() throws Exception {
        token = null;
        new ParamsAdapter(this, "xxxx", tmpDir, ipaFile, null, true, true, null,
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), false);
    }

    @Test(expected = AbortException.class)
    public void testConstructorEmptyToken() throws Exception {
        token = null;
        new ParamsAdapter(this, "", tmpDir, ipaFile, "binary ", true, true, null,
                username, password, showStatusMessages, stopTestsForStatusMessage, new ProxySettings(), true);
    }

    @Test
    public void testHasFile() throws Exception {
        Assert.assertTrue(ParamsAdapter.hasFile(this.file.getParentFile(), new File("."), this.file.getName(), "name"));
    }

    @Test
    public void testHasFileAbsoluteNonExistant() throws Exception {
    	File differentDir = Files.createTempDir();
    	differentDir.deleteOnExit();
    	File tmpFile = new File(differentDir, "tmpxxxx");
    	tmpFile.createNewFile();
    	String tmpFilePath = tmpFile.getAbsolutePath();
    	tmpFile.deleteOnExit();
    	tmpFile.delete();
    	differentDir.delete();
    	System.out.println(tmpDir.getAbsolutePath());
    	System.out.println(tmpFile.getAbsolutePath());
        Assert.assertFalse(
                ParamsAdapter.hasFile(workspace, tmpDir, tmpFilePath, "name"));
    }

    @Test
    public void testHasFileAbsolute() throws Exception {
        File file = new File(tmpDir, "tst");
        file.createNewFile();
        file.deleteOnExit();
        Assert.assertTrue(ParamsAdapter.hasFile(tmpDir, new File("."), file.getAbsolutePath(), "name"));
        file.delete();
    }

    @Test
    public void testHasFileNonExistant() throws Exception {
        File file = new File(tmpDir, "tst");
        Assert.assertFalse(ParamsAdapter.hasFile(file.getParentFile(), tmpDir, file.getName(), "name"));
    }

    @Test
    public void testToString() throws Exception {
        ParamsAdapter params = new ParamsAdapter(this, "", tmpDir, ipaFile,
                "binary ", true, true, null, username, password, showStatusMessages, stopTestsForStatusMessage,
                new ProxySettings(), true);
        Assert.assertNotNull(params.toString());
    }

    @Override
    public String getApiKey() {
        return token;
    }

    @Override
    public String getApiUrl() {
        return url;
    }

    @Override
    public File getArtifactsDir() {
        return artifactsDir;
    }

    @Override
    public String getDescription() {
        return "desc";
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String getGroup() {
        return "group";
    }

    @Override
    public int getScoreThreshold() {
        return score;
    }

    @Override
    public int getWaitMinutes() {
        return minutes;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isShowStatusMessages() {
        return showStatusMessages;
    }

    public void setShowStatusMessages(boolean showStatusMessages) {
        this.showStatusMessages = showStatusMessages;
    }

    @Override
    public String getStopTestsForStatusMessage() {
        return stopTestsForStatusMessage;
    }

    public void setStopTestsForStatusMessage(String stopTestsForStatusMessage) {
        this.stopTestsForStatusMessage = stopTestsForStatusMessage;
    }

    @Override
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public ProxySettings getProxySettings() {
        return new ProxySettings();
    }

    @Override
    public boolean isProxyEnabled() {
        return false;
    }

    @Override
    public boolean isValidateDnsUrlConnectionEnabled() {
        return false;
    }

}
