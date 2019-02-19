# ezdownload
使用: 
在MainActivity的onCreate()中 调用DownloadMgr.getInstance().bindService();      
在 onDestory() 中调用DownloadMgr.getInstance().unBindService();     
下载：DownloadMgr.getInstance().download();      
取消：DownloadMgr.getInstance().cancel();      
监听：DownloadMgr.getInstance().addDownloadListener();   
