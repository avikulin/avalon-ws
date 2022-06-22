package DAL.DataEntities.Enums;

import DAL.Contracts.SelfDescriptable;

public enum OsiLayer implements SelfDescriptable {
    LEVEL1("Физический (L1)"),
    LEVEL2("Канальный (L2)"),
    LEVEL3("Сетевой (L3)"),
    LEVEL4("Транспортный (L4)"),
    LEVEL5("Сессионный (L5)"),
    LEVEL6("Презентационный (L6)"),
    LEVEL7("Пользовательский (L7)");

    private final String description;

    OsiLayer(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
