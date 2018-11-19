package com.junmeng.bttv.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

public class LocalFileUtil {

    /**

     * 获取视频文件截图

     *

     * @param path 视频文件的路径

     * @return Bitmap 返回获取的Bitmap

     */

    public  static Bitmap getVideoThumb(String path) {

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return  media.getFrameAtTime();

    }

    /**
     * 获取视频文件
     *
     * @param list
     * @param file
     * @return
     */
    public static List<MediaBean> getVideoFile(final List<MediaBean> list, File file) {

        File[] dd=file.listFiles();
        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {

                String name = file.getName();

                int i = name.indexOf('.');
                if (i != -1) {
                    name = name.substring(i);
                    if (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp") || name.equalsIgnoreCase(".wmv")
                            || name.equalsIgnoreCase(".ts") || name.equalsIgnoreCase(".rmvb")
                            || name.equalsIgnoreCase(".mov") || name.equalsIgnoreCase(".m4v")
                            || name.equalsIgnoreCase(".avi") || name.equalsIgnoreCase(".m3u8")
                            || name.equalsIgnoreCase(".3gpp") || name.equalsIgnoreCase(".3gpp2")
                            || name.equalsIgnoreCase(".mkv") || name.equalsIgnoreCase(".flv")
                            || name.equalsIgnoreCase(".divx") || name.equalsIgnoreCase(".f4v")
                            || name.equalsIgnoreCase(".rm") || name.equalsIgnoreCase(".asf")
                            || name.equalsIgnoreCase(".ram") || name.equalsIgnoreCase(".mpg")
                            || name.equalsIgnoreCase(".v8") || name.equalsIgnoreCase(".swf")
                            || name.equalsIgnoreCase(".m2v") || name.equalsIgnoreCase(".asx")
                            || name.equalsIgnoreCase(".ra") || name.equalsIgnoreCase(".ndivx")
                            || name.equalsIgnoreCase(".xvid")) {
                        MediaBean video = new MediaBean();
                        file.getUsableSpace();
                        video.setMediaName(file.getName());
                        video.setPath(file.getAbsolutePath());
                        Log.e("CJT", "最后的大小" + "ScannerAnsyTask---视频名称--name--" + video.getPath());
                        list.add(video);
                        return true;
                    }
                    // 判断是不是目录
                } else if (file.isDirectory()) {
                    getVideoFile(list, file);
                }
                return false;
            }
        });

        return list;
    }


    public static class MediaBean{

        private String mediaName;
        private String path;

        public String getMediaName() {
            return mediaName;
        }

        public void setMediaName(String mediaName) {
            this.mediaName = mediaName;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
