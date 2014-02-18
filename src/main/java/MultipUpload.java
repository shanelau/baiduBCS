import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.model.X_BS_ACL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: liu.xing
 * Date: 14-1-20
 * Time: 下午7:23
 * coding for fun and coding my life!
 */
public class MultipUpload {
    static String filePrefix = "E:/站点/innos/js";
    static int len = filePrefix.length();
    private static ArrayList filelist = new ArrayList();
    static String bucket = "innosrom";     //文件仓库名
    static String bcsDir = "/webDesgin/01/js/";  //文件存放目录

    static String object = "";             //文件对象本身
    static BaiduBCS bcs;
    static Properties pro;


    public static void main(String[] args) {
        MultipUpload mu = new MultipUpload();
        long a = System.currentTimeMillis();
        pro = mu.loadFileContentType();    //载入文件格式对应的文档类型
        bcs = Sample.init();              //初始化百度云存储环境
        mu.refreshFileList(filePrefix);   //遍历文件
        System.out.println(System.currentTimeMillis() - a);
    }

    /**
     * 遍历文件
     * @param strPath 文件夹路径
     */
    public void refreshFileList(String strPath) {
        File dir = new File(strPath);
        File[] files = dir.listFiles();

        if (files == null)
            return;
        for (int i = 0; i < files.length; i++) {
            String absPath = files[i].getAbsolutePath();
            if (files[i].isDirectory()) {
                refreshFileList(absPath);
            } else {
                String strFileName = absPath.replace("\\", "/");
                object = bcsDir + strFileName.substring(len + 1);
                String contentType = getContentType(object,pro);
                System.out.println(object+"\t"+contentType);
                Sample.putObjectByFile(bcs, bucket, object, new File(absPath),contentType);
                Sample.putObjectPolicyByX_BS_ACL(bcs, bucket, object, X_BS_ACL.PublicReadWrite);
                //deleteAll(bcs,object);
            }
        }
    }

    /**
     * 根据文件名获取ContentType
     * @param object    文件名
     * @param pro      contentType-file 对应的 Properties对象
     * @return      ContentType
     */
    private String getContentType(String object,Properties pro) {

        if(object.contains(".")){
            int index = object.lastIndexOf(".");
            String suffix = object.substring(index+1);     //获取文件后缀
            Object contentType = pro.get(suffix);
            if(contentType != null)
                return contentType.toString();
        }
        return "text/html";
    }


    /**
     * 载入ContentType 文件
     * @return
     */
    public Properties loadFileContentType(){
        InputStream is = getClass().getClassLoader().getSystemResourceAsStream("contentType.proerties");
        Properties pro = new Properties();
        try {
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  pro;
    }

    public void deleteAll(BaiduBCS bcs,String object){
        Sample.deleteObject(bcs,object);
    }


}
