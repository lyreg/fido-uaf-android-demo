package org.lyreg.fido_uaf_android_demo.uaf;

/**
 * Created by Administrator on 2016/1/14.
 */
public enum  UAFIntentType {

    DISCOVER(0, "DISCOVER"),
    DISCOVER_RESULT(1, "DISCOVER_RESULT"),
    CHECK_POLICY(2, "CHECK_POLICY"),
    CHECK_POLICY_RESULT(3, "CHECK_POLICY_RESULT"),
    UAF_OPERATION(4, "UAF_OPERATION"),
    UAF_OPERATION_RESULT(5, "UAF_OPERATION_RESULT"),
    UAF_OPERATION_COMPLETION_STATUS(6, "UAF_OPERATION_COMPLETION_STATUS");

    private final int       VALUE;
    private final String    DESCRIPTION;

    UAFIntentType(final int value, final String description) {
        this.VALUE = value;
        this.DESCRIPTION = description;
    }

    public int getValue() { return VALUE; }

    public String getDescription() { return DESCRIPTION; }

    public UAFIntentType getByValue(final int value) {
        for (final UAFIntentType uafIntentType : values()) {
            if (uafIntentType.getValue() == value) {
                return uafIntentType;
            }
        }
        throw new IllegalArgumentException("Invalid uaf intent type value: " + value);
    }

    public UAFIntentType getByDescription(final String description) {
        for (final UAFIntentType uafIntentType : values()) {
            if (uafIntentType.getDescription().equals(description)) {
                return uafIntentType;
            }
        }
        throw new IllegalArgumentException("Invalid uaf intent type description: " + description);
    }
}
