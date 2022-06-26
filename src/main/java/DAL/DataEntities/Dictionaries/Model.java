package DAL.DataEntities.Dictionaries;

import DAL.DataEntities.Enums.OsiLayer;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;

@Entity
@Table(name = "DICT_MODELS",
       uniqueConstraints = {@UniqueConstraint(name = "MODEL_VENDOR_UC", columnNames = {"MODEL_CODE","VENDOR_ID"})})
public class Model {
    @XmlAttribute
    @Id
    private String id;

    @XmlAttribute
    @Column(name = "MODEL_CODE", nullable = false, scale = 100)
    private String modelCode;

    @XmlAttribute
    @OneToOne
    @JoinColumn(name = "VENDOR_ID", nullable = false)
    private Vendor vendor;

    @XmlAttribute
    @Column(name = "MODEL_DESCRIPTION", nullable = false)
    private String modelDescription;

    @XmlAttribute
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "LAYER_NUM", nullable = false)
    private OsiLayer layerNum;

    @XmlAttribute
    @Column(name = "PORTS_100MBPS")
    private int num100MbpsPorts;

    @XmlAttribute
    @Column(name = "PORTS_10GBPS")
    private int num10GbpsPorts;

    @XmlAttribute
    @Column(name = "PORTS_POE")
    private int numPoEPorts;

    @XmlAttribute
    @Column(name = "IMAGE_URL", nullable = false, length = 200)
    private String imageUrl;

    @Transient
    private int totalNumPorts;

    public String getId() {
        return id;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public OsiLayer getLayerNum() {
        return layerNum;
    }

    public void setLayerNum(OsiLayer layerNum) {
        this.layerNum = layerNum;
    }

    public int getNum100MbpsPorts() {
        return num100MbpsPorts;
    }

    public void setNum100MbpsPorts(int num100MbpsPorts) {
        this.num100MbpsPorts = num100MbpsPorts;
    }

    public int getNum10GbpsPorts() {
        return num10GbpsPorts;
    }

    public void setNum10GbpsPorts(int num10GbpsPorts) {
        this.num10GbpsPorts = num10GbpsPorts;
    }

    public int getNumPoEPorts() {
        return numPoEPorts;
    }

    public void setNumPoEPorts(int numPoEPorts) {
        this.numPoEPorts = numPoEPorts;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getTotalNumPorts() {
        return num10GbpsPorts + num100MbpsPorts + numPoEPorts;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id='" + id + '\'' +
                ", modelCode='" + modelCode + '\'' +
                ", vendor=" + vendor +
                ", modelDescription='" + modelDescription + '\'' +
                ", layerNum=" + layerNum +
                ", num100MbpsPorts=" + num100MbpsPorts +
                ", num10GbpsPorts=" + num10GbpsPorts +
                ", numPoEPorts=" + numPoEPorts +
                ", imageUrl='" + imageUrl + '\'' +
                ", totalNumPorts=" + totalNumPorts +
                '}';
    }
}
