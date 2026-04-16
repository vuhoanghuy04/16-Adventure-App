package com.example.a16adventure.models;

import java.io.Serializable;
import java.util.List;

/**
 * LandmarkGallery – Model cho gallery ảnh + video của một địa danh.
 *
 * videoRawId: R.raw.xxx (int).  0 = không có video.
 */
public class LandmarkGallery implements Serializable {

    private String       id;
    private String       name;
    private String       description;
    private String       district;
    private List<String> imageUrls;
    private int          videoRawId;   // R.raw.xxx, 0 nếu không có video
    private String       videoTitle;

    public LandmarkGallery() {}

    /** Constructor dùng khi KHÔNG có video */
    public LandmarkGallery(String id, String name, String description, String district,
                           List<String> imageUrls) {
        this(id, name, description, district, imageUrls, 0, null);
    }

    /** Constructor dùng khi CÓ video */
    public LandmarkGallery(String id, String name, String description, String district,
                           List<String> imageUrls, int videoRawId, String videoTitle) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.district    = district;
        this.imageUrls   = imageUrls;
        this.videoRawId  = videoRawId;
        this.videoTitle  = videoTitle;
    }

    public String       getId()          { return id; }
    public String       getName()        { return name; }
    public String       getDescription() { return description; }
    public String       getDistrict()    { return district; }
    public List<String> getImageUrls()   { return imageUrls; }
    public int          getVideoRawId()  { return videoRawId; }
    public String       getVideoTitle()  { return videoTitle; }
    public boolean      hasVideo()       { return videoRawId != 0; }
}
