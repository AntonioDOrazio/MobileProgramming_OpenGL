package it.tiburtinavalley.mpopengl;

import android.content.Context;

public class ContextUtil {
        private Context appContext;

        private ContextUtil(){}

        public void init(Context context){
            if(appContext == null){
                appContext = context;
            }
        }

        private Context getContext(){
            return appContext;
        }

        public static Context get(){
            return getInstance().getContext();
        }

        private static ContextUtil instance;

        public static ContextUtil getInstance(){
            return instance == null ?
                    (instance = new ContextUtil()):
                    instance;
        }
}
