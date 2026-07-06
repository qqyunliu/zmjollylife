package com.easyjava.entity.query;

import java.util.Date;

/**
 * @Description:用户信息查询对象
 */
public class UserInfoquery extends BaseQuery {
    
    private String userId;

    private String userIdFuzzy;

    private String nickId;

    private String nickIdFuzzy;

    private String avatar;

    private String sex;

    private Date birthday;

    private String birthdayStart;

    private String birthdayEnd;

    private String school;

    private String schoolFuzzy;

    private String personIntroduction;

    private String personIntroductionFuzzy;

    private String noticeInfo;

    private String noticeInfoFuzzy;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserIdFuzzy(String userIdFuzzy) {
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy() {
        return this.userIdFuzzy;
    }

    public void setNickId(String nickId) {
        this.nickId = nickId;
    }

    public String getNickId() {
        return this.nickId;
    }

    public void setNickIdFuzzy(String nickIdFuzzy) {
        this.nickIdFuzzy = nickIdFuzzy;
    }

    public String getNickIdFuzzy() {
        return this.nickIdFuzzy;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return this.sex;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthdayStart(String birthdayStart) {
        this.birthdayStart = birthdayStart;
    }

    public String getBirthdayStart() {
        return this.birthdayStart;
    }

    public void setBirthdayEnd(String birthdayEnd) {
        this.birthdayEnd = birthdayEnd;
    }

    public String getBirthdayEnd() {
        return this.birthdayEnd;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSchool() {
        return this.school;
    }

    public void setSchoolFuzzy(String schoolFuzzy) {
        this.schoolFuzzy = schoolFuzzy;
    }

    public String getSchoolFuzzy() {
        return this.schoolFuzzy;
    }

    public void setPersonIntroduction(String personIntroduction) {
        this.personIntroduction = personIntroduction;
    }

    public String getPersonIntroduction() {
        return this.personIntroduction;
    }

    public void setPersonIntroductionFuzzy(String personIntroductionFuzzy) {
        this.personIntroductionFuzzy = personIntroductionFuzzy;
    }

    public String getPersonIntroductionFuzzy() {
        return this.personIntroductionFuzzy;
    }

    public void setNoticeInfo(String noticeInfo) {
        this.noticeInfo = noticeInfo;
    }

    public String getNoticeInfo() {
        return this.noticeInfo;
    }

    public void setNoticeInfoFuzzy(String noticeInfoFuzzy) {
        this.noticeInfoFuzzy = noticeInfoFuzzy;
    }

    public String getNoticeInfoFuzzy() {
        return this.noticeInfoFuzzy;
    }
}
