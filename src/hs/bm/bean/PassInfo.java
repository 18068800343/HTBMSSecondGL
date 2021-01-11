package hs.bm.bean;

public class PassInfo {

	/**编号*/
	private String pass_id;
	/***/
	private String highway_id;
	/***/
	private String manage_id;
	/***/
	private String section_id;
	/***/
	private String zone_id;
	/***/
	private String pass_no;
	/***/
	private String pass_name;
	/**桩号*/
	private String stub_no;
	/**通道类型*/
	private String pass_type;
	/***/
	private String skew_angle;
	public String getSkew_angle() {
		return skew_angle;
	}

	public void setSkew_angle(String skew_angle) {
		this.skew_angle = skew_angle;
	}

	/***/
	private String span_build;
	/***/
	private String use_type;
	/**检查人*/
	private String chk_person;
	/**记录人*/
	private String record_person;
	/**复核人*/
	private String review_person;
	/**负责人*/
	private String response_person;
	/**经度*/
	private String longitude;
	/**纬度*/
	private String latitude;
    private String construct_ym;
	public String getConstruct_ym() {
		return construct_ym;
	}

	public void setConstruct_ym(String construct_ym) {
		this.construct_ym = construct_ym;
	}

	public void setPass_id(String pass_id){
		this.pass_id=pass_id;
	}

	public String getPass_id(){
		return pass_id;
	}

	public void setHighway_id(String highway_id){
		this.highway_id=highway_id;
	}

	public String getHighway_id(){
		return highway_id;
	}

	public void setManage_id(String manage_id){
		this.manage_id=manage_id;
	}

	public String getManage_id(){
		return manage_id;
	}

	public void setSection_id(String section_id){
		this.section_id=section_id;
	}

	public String getSection_id(){
		return section_id;
	}

	public void setZone_id(String zone_id){
		this.zone_id=zone_id;
	}

	public String getZone_id(){
		return zone_id;
	}

	public void setPass_no(String pass_no){
		this.pass_no=pass_no;
	}

	public String getPass_no(){
		return pass_no;
	}

	public void setPass_name(String pass_name){
		this.pass_name=pass_name;
	}

	public String getPass_name(){
		return pass_name;
	}

	public void setStub_no(String stub_no){
		this.stub_no=stub_no;
	}

	public String getStub_no(){
		return stub_no;
	}

	public void setPass_type(String pass_type){
		this.pass_type=pass_type;
	}

	public String getPass_type(){
		return pass_type;
	}

	public void setSpan_build(String span_build){
		this.span_build=span_build;
	}

	public String getSpan_build(){
		return span_build;
	}

	public void setUse_type(String use_type){
		this.use_type=use_type;
	}

	public String getUse_type(){
		return use_type;
	}

	public void setChk_person(String chk_person){
		this.chk_person=chk_person;
	}

	public String getChk_person(){
		return chk_person;
	}

	public void setRecord_person(String record_person){
		this.record_person=record_person;
	}

	public String getRecord_person(){
		return record_person;
	}

	public void setReview_person(String review_person){
		this.review_person=review_person;
	}

	public String getReview_person(){
		return review_person;
	}

	public void setResponse_person(String response_person){
		this.response_person=response_person;
	}

	public String getResponse_person(){
		return response_person;
	}

	public void setLongitude(String longitude){
		this.longitude=longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	public void setLatitude(String latitude){
		this.latitude=latitude;
	}

	public String getLatitude(){
		return latitude;
	}

}
