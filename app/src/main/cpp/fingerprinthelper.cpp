#include <jni.h>
#include <string>
#include <sys/system_properties.h>

using namespace std;

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_sunritel_fingerprinthelper_SettingsActivity_isCertificateDevice(JNIEnv *env,
                                                                         jobject thiz) {
    string model = "ro.product.model";
    string brand = "ro.product.brand";
    char prop_model[PROP_VALUE_MAX];
    char prop_brand[PROP_VALUE_MAX];
    __system_property_get(model.c_str(), prop_model);
    __system_property_get(brand.c_str(), prop_brand);
    bool isCertificateModel =
            strcmp(prop_model, "ASPE2201") == 0 || strcmp(prop_model, "ConnectPro_L") == 0;
    bool isCertificateBrand =
            strcmp(prop_brand, "AMGx13e") == 0 || strcmp(prop_brand, "G-mee") == 0;
    return isCertificateModel && isCertificateBrand;
}