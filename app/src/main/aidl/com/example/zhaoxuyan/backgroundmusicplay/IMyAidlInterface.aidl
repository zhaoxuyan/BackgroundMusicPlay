// IMyAidlInterface.aidl
package com.example.zhaoxuyan.backgroundmusicplay;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * 向外提供 可以让其他应用调用的服务
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void play();
}
