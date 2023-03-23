#include <jni.h>
#include <string>
#include <sys/system_properties.h>

using namespace std;

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    string device = "ro.product.model";
    string model = "ro.product.model";
    string brand = "ro.product.brand";
    char prop_device[PROP_VALUE_MAX];
    char prop_model[PROP_VALUE_MAX];
    char prop_brand[PROP_VALUE_MAX];
    __system_property_get(device.c_str(), prop_device);
    __system_property_get(model.c_str(), prop_model);
    __system_property_get(brand.c_str(), prop_brand);

    bool isCertificateBrand =
            strcmp(prop_brand, "AMGx13e") == 0 || strcmp(prop_brand, "G-mee") == 0;

    if (!isCertificateBrand) {
        terminate();
    }

    return JNI_VERSION_1_6;
}