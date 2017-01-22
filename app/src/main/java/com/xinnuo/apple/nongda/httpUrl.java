package com.xinnuo.apple.nongda;

/**
 * Created by apple on 16/10/28.
 */

public  class httpUrl {
    //正式服务器
    private static final String formal = "http://123.56.46.38/JLCitySchool/";
    //测试服务器
    private static final String test = "http://192.168.1.166:9090/nongda/";
    private static final String my =   "http://192.168.2.107:8080/nongda/";
    private static final String url = test;
    //MainActivity登录
    public static final String loginUrl = url+"iosAppLogin_iosLogin.action";
    //ClassSignActivity课堂考勤
    public static final String classListUrl = url+"iosAppStudentInfo_sweepTheCodeSignIn.action";
    //SleepSignActivity归寝考勤记录查询
    public  static  final  String sleepSignUrl = url+"iosAppStudentInfo_recordBackDormitory.action";
    //SignSleepActivity归寝查坐标
    public  static  final  String SignSleepUrl = url+"iosAppStudentInfo_nameAndDormitoryCoordinates.action";
    //signSleepActivity归寝立即签到
    public  static  final  String makeSignSleep = url +"iosAppStudentInfo_StudentBackToTheBedroom.action";

    //StuQRCodeActivity二维码签到
    public static final String StuQRCodeUrl = url+"iosAppStudentInfo_OrientationCheckin.action";
    //result签到成功提交签到
    public  static  final  String QRResultUrl = url+"iosAppStudentInfo_studentsSignIn.action";

    //stuTimeClassActivity课表
    public static  final String stuTimeClassUrl = url+"iosAppStudentInfo_kechengBiao.action";
    //ClassListActivity班级列表
    public static  final String ClassListUrl = url+"iosAppTeacherInfo_getTeacherIdQueryClassList.action";
    //StudentListActivity学生列表
    public static final String studentListUrl = url+"iosAppTeacherInfo_accordingToTheIdOfTheStudentClass.action";
    //StudentBinding学生绑定
    public static final String studentBindingUrl = url+"iosAppStudentInfo_studentVerification.action";
    //TeacherBinding教师绑定
    public static final String teacherBindingUrl = url+"teacherBinding_teacherBinding.action";
    //TeacherClub 教师俱乐部查询
    public static final String teacherClubUrl = url + "sportsClubAction_getSportClassByteacherId.action";
    //TeacherClubStudentNumber 教师俱乐部学生列表
    public static final String TeacherClubStudentNumberUrl = url + "sportsClubAction_getStudentListBySportClassId.action";
    //AttendanceDataStatisticsActivity 教师考勤数据统计
    public static final String AttendanceDataStatistics = url +"iosAppTeacherInfo_accordingToTheStateOfTeachersQuery.action";
    //studentPhysicalFitnessScore 体质测试总分
    public static final String StudentPhysicalFitnessScore = url + "iosAppTeacherInfo_queryStudentsInSportsClassList.action";
    //StudentDetailedScore 体质测试详细
    public static final String StudentDetailedScore = url + "iosAppTeacherInfo_studentIdQueryTheCervixGrades.action";
    //meetingInfo 会议列表
    public static final String MeetingInfo = url +"meetingInfoAction_allMeeting.action";
    //meetingConferenceList 会议名单
    public static final String MeetingConferenceList = url + "meetingInfoAction_getInfoByMeetingId.action";
    //MeetingLeave 教师会议请假
    public static final String MeetingLeave = url + "meetingInfoAction_getMeetingLeaveInfo.action";
    //TeachersGetCoordinateMeeting 获取教师定位坐标
    public static final String TeachersGetCoordinateMeeting = url + "meetingInfoAction_teachersGetCoordinateMeeting.action";
    //TeacherSign 教师签到
    public static final String TeacherSign = url + "meetingInfoAction_updatSgin.action";
    //TeacherModifyPassword 教师修改密码
    public static final String TeacherModifyPassword = url + "iosAppTeacherInfo_ChangeThePassword.action";
    //TeacherModifyPassword 教师修改密码
    public static final String StudentModifyPassword = url + "iosAppStudentInfo_ChangeThePassword.action";
    //TeacherSubstitute 代课详情
    public static final String TeacherSubstitute = url + "iosSubstitute_getSubstituteByTeacherId.action";
    //TeacherWithdrawSubstitute 教师取消申请代课
    public static final String TeacherWithdrawSubstitute = url +"iosSubstitute_getCancelSub.action";
    //TeacherSubstituteAdd 增加代课合课
    public static final String TeacherSubstituteAdd = url + "iosSubstitute_addSubstitute.action";
    //QueryAllTheeachers 查询全部教师
    public static final String QueryAllTheeachers = url + "meetingInfoAction_queryAllTheeachers.action";
    //TeacherAddSubstitute 教师申请代课或合课
    public static final String TeacherAddSubstitute = url + "iosSubstitute_addSubstitute.action";
    //QueryTeacherToordinates  查询教师坐标
    public static final String QueryTeacherToordinates = url + "iosAppStudentInfo_OrientationCheckinThree.action";
    //TeacherClassList 教师课程签到 课程列表
    public static final String TeacherClassList = url + "iosAppTeacherInfo_keChengChaXun.action";
    //TeacherAttendanceList 教师查看签到状态
    public static final String TeacherAttendanceList = url + "iosAppTeacherInfo_attendanceList.action";
    //TeacherCourseStart 教师课程开始
    public static final String TeacherCourseStart = url + "iosAppTeacherInfo_TeacherClassButton.action";
    //TeacherStartAttendance 教师开始考勤记录考勤时间
    public static final String TeacherStartAttendance = url + "iosAppTeacherInfo_theTeacherAttendanceStatistics.action";
    //TeacherEndOfCourse  //下课按钮
    public static final String TeacherEndOfCourse = url +"iosAppTeacherInfo_TheOriginalTeacherClassAttendance.action";
    //TeacherStartPositioning 教师开始签到
    public static final String TeacherStartPositioning = url + "iosAppTeacherInfo_OrientationCheckIn.action";
    //TeacherCloseLocation  教师关闭定位
    public static final String TeacherCloseLocation = url + "iosAppTeacherInfo_theLocationSignIn.action";
    //TeacherUpdateTheQrCodeKey
    public static final String TeacherUpdateTheQrCodeKey = url + "iosAppTeacherInfo_UpdateTheQrCodeKey.action";
    //TeacherSubstituteTeachersQuery  教师查询要为那个教师代课 或 合课
    public static final String TeacherSubstituteTeachersQuery = url + "iosSubstitute_SubstituteTeachersWillSubstituteWhoQuery.action";
    //QueryTeacherCoordinates  查询教师经纬度
    public static final String QueryTeacherCoordinates = url + "iosAppStudentInfo_OrientationCheckinThree.action";
    //UpdateTheQrCodeKey   跟新二维码秘钥
    public static final String UpdateTheQrCodeKey = url + "iosAppTeacherInfo_UpdateTheQrCodeKey.action";
    //StuLocationSignIn 定位签到
    public static final String StuLocationSignIn = url + "iosAppStudentInfo_OrientationCheckinTow.action";
    //StuClubList 俱乐部列表
    public static final String StuClubList = url + "iosAppStudentInfo_findTheClub.action";
    //StuAddClub 添加俱乐部
    public static final String StuAddClub = url + "iosAppStudentInfo_getInsertSportsclass.action";
    //StuApplicationForTransfer 申请调课
    public static final String StuApplicationForTransfer = url + "iosAppStudentInfo_toApplyForTheClass.action";
    //StuCampusDynamics 校园动态
    public static final String StuCampusDynamics = url + "iosAppStudentInfo_campusDynamicImg.action";
    //TeacherShow 教师风采
    public static final String TeacherShow = url + "iosAppStudentInfo_jiaoShiFengCais.action";
    // StuNews     消息
    public static final String StuNews = url + "iosAppTeacherInfo_seeAdministratorMessage.action";
    //WorkPlan g工作计划列表
    public static final String WorkPlan = url + "workplanAction_getAllWorkplans.action";
    //UpdateWorkPlan 更新工作计划表
    public static final String UpdateWorkPlan = url + "workplanAction_updateWorkplan.action";
    //SportCount 运动量查询
    public static final String SportCount = url + "iosAppStudentInfo_StudentsexerciseShow.action";
    //DeleteWorkPlan 删除工作计划
    public static final String DeleteWorkPlan = url + "workplanAction_delWorkplan.action";
    //AddWorkPlan 新增工作计划
    public static final String AddWorkPlan = url + "workplanAction_addWorkplan.action";
    //SportRules 运动量规则
    public static final String SportRules = url + "iosAppStudentInfo_StudentInquiryActivityRule.action";
    //SportRules 运动量提交
    public static final String SportUpload = url + "iosAppStudentInfo_NewStudentsexercise.action";
 }
