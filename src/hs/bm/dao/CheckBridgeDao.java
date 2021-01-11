package hs.bm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import com.alibaba.fastjson.JSON;

import hs.bm.bean.BrgSpanInfo;
import hs.bm.bean.ChkBrgDefect;
import hs.bm.bean.ChkBrgPhoto;
import hs.bm.bean.ChkBrgRecord;
import hs.bm.bean.ChkBrgRegular;
import hs.bm.bean.ChkProjectInfo;
import hs.bm.bean.ChkSpanRecord;
import hs.bm.bean.DefectCount;
import hs.bm.util.FileUtils;
import hs.bm.util.IDtool;
import hs.bm.vo.BridgeChk;
import hs.bm.vo.BridgeChkVo;
import hs.bm.vo.OperationConstruct;
import hs.bm.vo.ResObj;

public class CheckBridgeDao {
	
	private static CheckBridgeDao checkBridgeDao;
	
	public static CheckBridgeDao getInstance(){
		if(checkBridgeDao==null){
			checkBridgeDao=new CheckBridgeDao();
		}
		return checkBridgeDao;
	}
	
	public String getPerson(String prj_id){
		String sql = "select prj_member from chk_project_info where prj_id=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id});
		String names = "";
		try {
			while (rs.next()) {
				names= rs.getString("prj_member");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return names;
	}
	
	/**
	 * @param prj_id 老项目ID 
	 * @param dataSourceType 数据源类型,old为老,new为新 
	 */
	public static List<BridgeChkVo> getChkIdByPrjId(String prj_id,String dataSourceType){
		String sql = " select chk_id,cbr.bridge_id,bcai.bridge_name from chk_brg_regular cbr LEFT JOIN brg_card_admin_id bcai on cbr.bridge_id = bcai.bridge_id where prj_id =? ";
		MyDataOperation dataOperation; 
		if("new".equals(dataSourceType)) {
			dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		}else {
			dataOperation = new MyDataOperation(MyDataSourceCopy.getInstance().getConnection());
		}
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id});
		List<BridgeChkVo> bridgeChkVos = new ArrayList<>();
		try {
			while (rs.next()) {
				BridgeChkVo bridgeChkVo = new BridgeChkVo();
				String chkId  = rs.getString("chk_id");
				bridgeChkVo.setChkId(chkId);
				bridgeChkVo.setBrgId(rs.getString("bridge_id"));
				bridgeChkVo.setBridgeName(rs.getString("bridge_name"));
				bridgeChkVos.add(bridgeChkVo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return bridgeChkVos;
	}
	
	public static String getNewChkIdByBrgIdAndPrjId(String prj_id,String brgId){
		String sql = " select chk_id from chk_brg_regular where prj_id =? and bridge_id=? ";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id,brgId});
		String chkId ="";
		try {
			if (rs.next()) {
			  chkId  = rs.getString("chk_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return chkId;
	}
	
	
	//@author xianing 
	public String getPrjDesc(String prj_id){
		String sql = "select prj_desc from chk_project_info where prj_id=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id});
		String info = "";
		try {
			while (rs.next()) {
				info= rs.getString("prj_desc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return info;
	}
	
	public List<BrgSpanInfo> getSpan(String bridge_id){
		String sql = "select * from brg_span_info where bridge_id=? ORDER BY span_no";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{bridge_id});
		List<BrgSpanInfo> lb=new ArrayList<BrgSpanInfo>();
		try {
			while (rs.next()) {
				BrgSpanInfo dmd = new BrgSpanInfo();
				dmd.setS_id(rs.getString("s_id"));
				dmd.setBridge_id(rs.getString("bridge_id"));
				dmd.setDirection(rs.getString("direction"));
				dmd.setSpan_no(rs.getString("span_no"));
				dmd.setBrg_type_id(rs.getString("brg_type_id"));
				lb.add(dmd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return lb;
	}
	
	
	 public ResObj CheckState(String username,OperationConstruct oc){
	    	ResObj ro = new ResObj();
	    	ro.setError(1);
	    	ro.setSuccess("fail");
	    	String sql="select a.*,b.* from (select * from chk_project_info where prj_id=? and (prj_charge_man like '%$%' or prj_member like '%$%')) as a RIGHT JOIN "
	    			+ "(select * from chk_brg_regular where chk_id=?) as b on 1=1;";
	    	sql=sql.replaceAll("\\$", username);
	    	MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
			ResultSet rs=dataOperation.executeQuery(sql,new String[]{oc.getPrj_id(),oc.getChk_id()});
			BridgeChk bc = new BridgeChk();
			bc.setChk_id(oc.getChk_id());
			bc.setChk_type(oc.getChk_type());
			bc.setPrj_desc(oc.getPrj_desc());
			bc.setPrj_id(oc.getPrj_id());
			try {
				if(rs.next()){
					ro.setSuccess("success");
					bc.setCheck_date(rs.getString("check_date"));
					bc.setAudit_state(rs.getString("audit_state"));
					ro.setObj(bc);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataOperation.close();
			return ro;
	    }
	 /**
	  * @author Administrator
	  * @param bc 桥梁检查信息对象
	  * @param dataSourceType 数据源类型 old为老,new为新
	  * @return
	  */
	 public BridgeChk getAllSpansData(BridgeChk bc){
		 String sql = "select * from chk_span_record where chk_id=?";
		 MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
		 
		 ResultSet rs = dataOperation.executeQuery(sql, new String[]{bc.getChk_id()});
		 List<ChkSpanRecord> lb=new ArrayList<ChkSpanRecord>();
		 try {
			 while (rs.next()) {
				 ChkSpanRecord dmd = new ChkSpanRecord();
				 dmd.setSpan_chk_id(rs.getString("span_chk_id"));
				 dmd.setChk_id(rs.getString("chk_id"));
				 dmd.setDirection(rs.getString("direction"));
				 dmd.setSpan_no(rs.getString("span_no"));
				 dmd.setSpan_build(rs.getString("span_build"));
				 dmd.setSpan_top_struct(rs.getString("span_top_struct"));
				 dmd.setSpan_down_struct(rs.getString("span_down_struct"));
				 dmd.setSpan_memo(rs.getString("span_memo"));
				 dmd.setSpan_suggestion(rs.getString("span_suggestion"));
				 dmd.setChk_time(rs.getString("chk_time"));
				 dmd.setChk_weather(rs.getString("chk_weather"));
				 dmd.setTemp(rs.getString("temp"));
				 dmd.setChk_state(rs.getString("chk_state"));
				 dmd.setRecheck_person(rs.getString("recheck_person"));
				 dmd.setRecord_person(rs.getString("record_person"));
				 lb.add(dmd);
			 }
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }
		 dataOperation.close();
		 bc.setSpans(lb);
		 return bc;
		 
	 }
	 /**
	  * @author Administrator
	  * @param bc 桥梁检查信息对象
	  * @param dataSourceType 数据源类型 old为老,new为新
	  * @return
	  */
	 public BridgeChk getAllSpansData(BridgeChk bc,String dataSourceType){
		 String sql = "select * from chk_span_record where chk_id=?";
		 MyDataOperation dataOperation; 
			if("new".equals(dataSourceType)) {
				dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
			}else {
				dataOperation = new MyDataOperation(MyDataSourceCopy.getInstance().getConnection());
			}
			ResultSet rs = dataOperation.executeQuery(sql, new String[]{bc.getChk_id()});
			List<ChkSpanRecord> lb=new ArrayList<ChkSpanRecord>();
			try {
				while (rs.next()) {
					ChkSpanRecord dmd = new ChkSpanRecord();
					dmd.setSpan_chk_id(rs.getString("span_chk_id"));
					dmd.setChk_id(rs.getString("chk_id"));
					dmd.setDirection(rs.getString("direction"));
					dmd.setSpan_no(rs.getString("span_no"));
					dmd.setSpan_build(rs.getString("span_build"));
					dmd.setSpan_top_struct(rs.getString("span_top_struct"));
					dmd.setSpan_down_struct(rs.getString("span_down_struct"));
					dmd.setSpan_memo(rs.getString("span_memo"));
					dmd.setSpan_suggestion(rs.getString("span_suggestion"));
					dmd.setChk_time(rs.getString("chk_time"));
					dmd.setChk_weather(rs.getString("chk_weather"));
					dmd.setTemp(rs.getString("temp"));
					dmd.setChk_state(rs.getString("chk_state"));
					dmd.setRecheck_person(rs.getString("recheck_person"));
					dmd.setRecord_person(rs.getString("record_person"));
					lb.add(dmd);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataOperation.close();
		 bc.setSpans(lb);
		 return bc;
		 
	 }
	 public ChkSpanRecord getChkSpanRecordByChkIdDirection(String chk_id,String direction,String span_no){
		 String sql = "select * from chk_span_record where chk_id=? and direction=? and span_no=?";
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
			ResultSet rs = dataOperation.executeQuery(sql, new String[]{chk_id,direction,span_no});
			ChkSpanRecord dmd = null;
			try {
				if (rs.next()) {
				    dmd = new ChkSpanRecord();
					dmd.setSpan_chk_id(rs.getString("span_chk_id"));
					dmd.setChk_id(rs.getString("chk_id"));
					dmd.setDirection(rs.getString("direction"));
					dmd.setSpan_no(rs.getString("span_no"));
					dmd.setSpan_build(rs.getString("span_build"));
					dmd.setSpan_top_struct(rs.getString("span_top_struct"));
					dmd.setSpan_down_struct(rs.getString("span_down_struct"));
					dmd.setSpan_memo(rs.getString("span_memo"));
					dmd.setSpan_suggestion(rs.getString("span_suggestion"));
					dmd.setChk_time(rs.getString("chk_time"));
					dmd.setChk_weather(rs.getString("chk_weather"));
					dmd.setTemp(rs.getString("temp"));
					dmd.setChk_state(rs.getString("chk_state"));
					dmd.setRecheck_person(rs.getString("recheck_person"));
					dmd.setRecord_person(rs.getString("record_person"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataOperation.close();
		 return dmd;
		 
	 }
	 public List<ChkSpanRecord> getAllSpans(String chk_id){
		 String sql = "select * from chk_span_record where chk_id=?";
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
			ResultSet rs = dataOperation.executeQuery(sql, new String[]{chk_id});
			List<ChkSpanRecord> lb=new ArrayList<ChkSpanRecord>();
			try {
				while (rs.next()) {
					ChkSpanRecord dmd = new ChkSpanRecord();
					dmd.setSpan_chk_id(rs.getString("span_chk_id"));
					dmd.setChk_id(rs.getString("chk_id"));
					dmd.setDirection(rs.getString("direction"));
					dmd.setSpan_no(rs.getString("span_no"));
					dmd.setSpan_build(rs.getString("span_build"));
					dmd.setSpan_top_struct(rs.getString("span_top_struct"));
					dmd.setSpan_down_struct(rs.getString("span_down_struct"));
					dmd.setSpan_memo(rs.getString("span_memo"));
					dmd.setSpan_suggestion(rs.getString("span_suggestion"));
					dmd.setChk_time(rs.getString("chk_time"));
					dmd.setChk_weather(rs.getString("chk_weather"));
					dmd.setTemp(rs.getString("temp"));
					dmd.setChk_state(rs.getString("chk_state"));
					dmd.setRecheck_person(rs.getString("recheck_person"));
					dmd.setRecord_person(rs.getString("record_person"));
					lb.add(dmd);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataOperation.close();
		 return lb;
		 
	 }
	 
	 public int addSpan(ChkSpanRecord cs){
		 String sql = "insert into chk_span_record(span_chk_id,chk_id,direction,span_no,span_build,span_top_struct,span_down_struct,span_memo,span_suggestion,chk_time,chk_weather,temp,chk_state,record_person,recheck_person) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
			//String uuid=UUID.randomUUID().toString().replaceAll("-", "");
			int i = dataOperation.executeUpdate(sql, new String[]{
					cs.getSpan_chk_id(),
					cs.getChk_id(),
					cs.getDirection(),
					cs.getSpan_no(),
					cs.getSpan_build(),
					cs.getSpan_top_struct(),
					cs.getSpan_down_struct(),
					cs.getSpan_memo(),
					cs.getSpan_suggestion(),
					cs.getChk_time(),
					cs.getChk_weather(),
					cs.getTemp(),
					cs.getChk_state(),
					cs.getRecord_person(),
					cs.getRecheck_person()
			});
			dataOperation.close();
			return i;
	 }
	 public int editSpan(ChkSpanRecord cs){
		 String sql = "update chk_span_record set span_build=?,span_top_struct=?,span_down_struct=?,span_memo=?,span_suggestion=?,chk_time=?,chk_weather=?,temp=?,chk_state=?,recheck_person=?,pdf=? where span_chk_id=?";
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
			//String uuid=UUID.randomUUID().toString().replaceAll("-", "");
			int i = dataOperation.executeUpdate(sql, new String[]{
					cs.getSpan_build(),
					cs.getSpan_top_struct(),
					cs.getSpan_down_struct(),
					cs.getSpan_memo(),
					cs.getSpan_suggestion(),
					cs.getChk_time(),
					cs.getChk_weather(),
					cs.getTemp(),
					cs.getChk_state(),
					cs.getRecheck_person(),
					null,
					cs.getSpan_chk_id()
			});
			dataOperation.close();
			return i;
	 }
	 public int delSpan(String span_chk_id){
		 String sql = "delete from chk_span_record where span_chk_id=?";
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
			int i = dataOperation.executeUpdate(sql, new String[]{
					span_chk_id
			});
			dataOperation.close();
			return i;
	 }
	 
	 public String listToStr( List<String> ll ){
		 int size = ll.size();
			String res = "";
			for(int i=0;i<size;i++){
				if(i==(size-1)){
					res += ll.get(i);
				}else{
					res += ll.get(i)+",";
				}
			}
			return res;
	 }
	 
	 public int overCheck(OperationConstruct oc,String chk_id){
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
			String sql = "select d1.bridge_pile_no,d1.bridge_id,d1.bridge_name,d1.beneath_path_name,d2.bridge_len,d1.build_year "
					+"from brg_card_admin_id d1,brg_card_struct_tech d2 where d1.bridge_id=d2.bridge_id and d1.bridge_id=?";
			ResultSet rs = dataOperation.executeQuery(sql, new String[]{oc.getId()});
			String bridge_pile_no= "";
			String beneath_path_name= "";
			String bridge_len= "";
			String build_year= "";
			String down_struct = "";
			String top_struct = "";
			try {
				while(rs.next()){
					bridge_pile_no = rs.getString("bridge_pile_no");
					beneath_path_name = rs.getString("beneath_path_name");
					bridge_len = rs.getString("bridge_len");
					build_year = rs.getString("build_year");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			down_struct = listToStr(getDownConstruct(oc.getId())); 
			
			sql = "select * from brg_card_top_struct where bridge_id=?";
			List<String> ll = new ArrayList<String>();
			Set<String> set = new HashSet<String>();
			rs = dataOperation.executeQuery(sql, new String[]{oc.getId()});
			try {
				while(rs.next()){
					String top_struct_type = rs.getString("top_struct_type");
					if(top_struct_type!=null && !top_struct_type.equals("")){
						set.add(top_struct_type);
					}
				 }
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ll.addAll(set);
			top_struct = listToStr(ll);
			sql = "UPDATE chk_brg_regular set stub_no=?,down_lane_name=?,bridge_len=?,construct_ym=?,line_no=?,line_name=?,top_struct_type=?,down_struct_type=?,"
					+ "check_date=CURDATE(),audit_state='1' where chk_id=?";
			int i = dataOperation.executeUpdate(sql, new String[]{
					bridge_pile_no,
					beneath_path_name,
					bridge_len,
					build_year,
					oc.getLine_no(),
					oc.getLine_name(),
					top_struct,
					down_struct,
					chk_id
			});
			dataOperation.close();
			return i;
		}
	 
//	 public List<SpanMem> spanCopy(String chk_type,String bridge_id,String direction,String span_no){
//		 String sql="select span_chk_id from chk_span_record where chk_id="
//		 		+ "(select b.chk_id from chk_project_info as a,chk_brg_regular as b "
//		 		+ "where b.bridge_id=? and a.prj_id=b.prj_id and a.chk_type=? and a.prj_state='1'"
//		 		+ "ORDER BY prj_complete_tm desc LIMIT 0,1) and direction=? and span_no=?";
//		 MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
//		 ResultSet rs = dataOperation.executeQuery(sql, new String[]{bridge_id,chk_type,direction,span_no});
//		 List<SpanMem> ls=new ArrayList<SpanMem>();
//		 try {
//				if (rs.next()) {
//					String span_chk_id=rs.getString("span_chk_id");
//					ls=CheckSpanDao.getInstance().initTable(span_chk_id);
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			dataOperation.close();
//			return ls;
//	 }
	 public List<String> getDefectPhoto(String span_chk_id){
		 String sql="select defect_photo from chk_brg_defect where mbr_chk_id in (select mbr_chk_id from chk_brg_record where span_chk_id=?)";
		 MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		 ResultSet rs = dataOperation.executeQuery(sql, new String[]{span_chk_id});
		 List<String> ls=new ArrayList<String>();
		 try {
				while (rs.next()) {
					ls.add(rs.getString("defect_photo"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataOperation.close();
			return ls;
		 
	 }
	 
	 public List<DefectCount> getDefectCount(String defect_serial){
		 String sql = "select * from defect_count where defect_serial=?";
		 MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		 ResultSet rs = dataOperation.executeQuery(sql, new String[]{defect_serial});
		 List<DefectCount> ll=new ArrayList<DefectCount>();
		 try {
				while (rs.next()) {
					DefectCount dCount = new DefectCount();
					dCount.setStruct_id(rs.getString("struct_id"));
					dCount.setChk_id(rs.getString("chk_id"));
					dCount.setMember_no(rs.getString("member_no"));
					dCount.setDefect_id(rs.getString("defect_id"));
					dCount.setDefect_record(rs.getString("defect_record"));
					dCount.setDefect_record_val(rs.getString("defect_record_val"));
					dCount.setDefect_record_type(rs.getString("defect_record_type"));
					dCount.setSave_date(rs.getString("save_date"));
					ll.add(dCount);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataOperation.close();
			return ll;
	 }
	 
	 public int saveDefectCount(List<DefectCount> ll, String defect_serial, String chk_id){
			String sql = "insert into defect_count (defect_serial,struct_id,chk_id,member_no,defect_id,defect_record,defect_record_val,defect_record_type,save_date) values (?,?,?,?,?,?,?,?,?)";
			MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(), false);
			int i=0;
			for(DefectCount dc: ll){
				i = dataOperation.executeUpdate(sql, new Object[]{
						defect_serial,
						dc.getStruct_id(),
						chk_id,
						dc.getMember_no(),
						dc.getDefect_id(),
						dc.getDefect_record(),
						dc.getDefect_record_val(),
						dc.getDefect_record_type(),
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				});
				if(i<0){
					break;
				}
			}
			dataOperation.close();
			return i;
		}
	 
	 public String getTopConstruct(String id, String direction, String span_no){
		 String sql = "select brg_type_name from dic_brg_struct_type_def where brg_type_id=(select brg_type_id from brg_span_info where bridge_id=? and direction=? and span_no=?)";
		 MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		 ResultSet rs = dataOperation.executeQuery(sql, new String[]{id, direction, span_no});
		 String type = "";
		 try {
			while(rs.next()){
				 type = rs.getString("brg_type_name");
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 dataOperation.close();
		 return type;
	 }
	 

	public List<String> getDownConstruct(String id) {
		String sql = "select * from brg_card_down_struct where bridge_id=?";
		List<String> ll = new ArrayList<String>();
		Set<String> set = new HashSet<String>();
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		 ResultSet rs = dataOperation.executeQuery(sql, new String[]{id});
		 try {
			while(rs.next()){
				String down_struct_type = rs.getString("down_struct_type");
				set.add(down_struct_type);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		 ll.addAll(set);
		 dataOperation.close();
		 return ll;
	}
	
	public Map<String, String> initPhoto(String id, String prj_id){
		System.out.println(id);
		System.out.println(prj_id);
		Map<String, String> map= new HashMap<String, String>();
		String sql = "select * from brg_prj_photo where bridge_id=? and prj_id=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{ id, prj_id });
		try {
			while(rs.next()){
				map.put(rs.getString("photo_type"), rs.getString("path"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		System.out.println(JSON.toJSONString(map));
		return map;
	}

	public void updatePhoto(String id, String prj_id, String path, String photo_type) {
		String sql = "update brg_prj_photo set path=? where bridge_id=? and prj_id=? and photo_type=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		int i = dataOperation.executeUpdate(sql, new String[]{path, id, prj_id, photo_type});
		if(i<=0){
			sql = "insert into brg_prj_photo(bridge_id, prj_id, path, photo_type) values(?,?,?,?)";
			i = dataOperation.executeUpdate(sql, new String[]{ id, prj_id, path, photo_type});
		}
		dataOperation.close();
	}
	
	public int changeState(String chk_id, String prj_id) {
		String sql = "update chk_brg_regular set audit_state='0' where chk_id=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
		int i = dataOperation.executeUpdate(sql, new String[]{
				chk_id
		});
		dataOperation.close();
		return i;
	}
	
	
	public int updateChkBrgRegular(ChkBrgRegular chkBrgRegular, String prj_id,String bridge_id,String chk_id) {
	// chk_id, prj_id, line_no, line_name, bridge_id, bridge_name, maintain_org, response_person, audit_state
		String sql = "update chk_brg_regular set "
				+ " stub_no=?,down_lane_name=?,"
				+ " bridge_len=?,main_span_struct=?,"
				+ " span_len=?,construct_ym=?,"
				+ " struct_span=?,top_struct_type=?,"
				+ " down_struct_type=?,last_maintain_date=?,"
				+ " last_check_date=?,check_date=?,"
				+ " climate=?,total_level=?,"
				+ " total_cleanliness_score=?,maintain_score=?,"
				+ " regular_maintain_rec=?,record_person=?,"
				+ " next_check_date=?,defect_desc=?,"
				+ " eval_level=?,pdf=?"
				+ " where chk_id=? ";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
		int i = dataOperation.executeUpdate(sql, new Object[]{
				chkBrgRegular.getStub_no(),chkBrgRegular.getDown_lane_name(),
				chkBrgRegular.getBridge_len(),chkBrgRegular.getMain_span_struct(),
				chkBrgRegular.getSpan_len(),chkBrgRegular.getConstruct_ym(),
				chkBrgRegular.getStruct_span(),chkBrgRegular.getTop_struct_type(),
				chkBrgRegular.getDown_struct_type(),chkBrgRegular.getLast_maintain_date(),
				chkBrgRegular.getLast_check_date(),chkBrgRegular.getCheck_date(),
				chkBrgRegular.getClimate(),chkBrgRegular.getTotal_level(),
				chkBrgRegular.getTotal_cleanliness_score(),chkBrgRegular.getMaintain_score(),
				chkBrgRegular.getRegular_maintain_rec(),chkBrgRegular.getRecord_person(),
				chkBrgRegular.getNext_check_date(),chkBrgRegular.getDefect_desc(),
				chkBrgRegular.getEval_level(),"",
				chk_id
		});
		dataOperation.close();
		return i;
	}
	
	public List<ChkProjectInfo> getPrj_desc(String chk_type){
		List<ChkProjectInfo> list=new ArrayList<ChkProjectInfo>();
		String prj_state="0";
		String sql="SELECT * FROM chk_project_info where prj_state=? and chk_type=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{ prj_state, chk_type });
		try {
			while(rs.next()){
				ChkProjectInfo chkProjectInfo = new ChkProjectInfo();
				chkProjectInfo.setPrj_desc(rs.getString("prj_desc"));
				chkProjectInfo.setPrj_establish_tm(rs.getString("prj_establish_tm"));
				chkProjectInfo.setPrj_charge_man(rs.getString("prj_charge_man"));
				chkProjectInfo.setPrj_member(rs.getString("prj_member"));
				list.add(chkProjectInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return list;
	}
	public String getPhone(String charge_man){
		List<String>list=new ArrayList<>();
		String sql="SELECT phone_no FROM sys_usr_usr_info a left join sys_org_usr_info b on a.org_usr_id = b.org_usr_id where a.usr_name=? ";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{charge_man});
		try {
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
        if(list.size()>0){
        	return list.get(0);
        }else{
        	return "";
        }
		
	}
	
	
	
	public String getFunctionA(String charge_man){
		List<String>list=new ArrayList<>();
		String sql="SELECT phone_no FROM sys_usr_usr_info a left join sys_org_usr_info b on a.org_usr_id = b.org_usr_id where a.usr_name=? ";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{charge_man});
		try {
			while(rs.next()){
				list.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
        if(list.size()>0){
        	return list.get(0);
        }else{
        	return "";
        }
		
	}

	public static String getNearOldPrjId(String bridge_id){
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		String sql = "select b.chk_id,b.prj_id from chk_project_info as a,chk_brg_regular as b where b.bridge_id=? and a.prj_id=b.prj_id and a.chk_type=? and a.prj_state='1' ORDER BY prj_establish_tm desc LIMIT 0,1";
//		System.out.println("获取数据");
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{bridge_id, "regular"});
//		String chk_id_old = null;
		String prj_id_old = null;
		try {
			if(rs.next()){
//				chk_id_old = rs.getString("chk_id");
				prj_id_old = rs.getString("prj_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			dataOperation.close();
		}
		return prj_id_old;
		
	}
	
	public List<ChkBrgRecord> getChkBrgRecordBySpanChkId(String span_chk_id,String dataSourceType)
	{
		//
		String sql = "select * from chk_brg_record where span_chk_id =? ";
		MyDataOperation dataOperation; 
		if("new".equals(dataSourceType)) {
			dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection(),false);
		}else {
			dataOperation = new MyDataOperation(MyDataSourceCopy.getInstance().getConnection(),false);
		}
		ResultSet rs = dataOperation.executeQuery(sql, new String[]
		{ span_chk_id});
		List<ChkBrgRecord> list = new ArrayList<>();
		try
		{
			while (rs.next())
			{
				ChkBrgRecord chkBrgRecord = new ChkBrgRecord();
				chkBrgRecord.setMbr_chk_id(rs.getString("mbr_chk_id"));
				chkBrgRecord.setSpan_chk_id(rs.getString("span_chk_id"));
				chkBrgRecord.setBridge_id(rs.getString("bridge_id"));
				chkBrgRecord.setDirection(rs.getString("direction"));
				chkBrgRecord.setSpan_no(rs.getString("span_no"));
				chkBrgRecord.setMbr_type(rs.getString("mbr_type"));
				chkBrgRecord.setMbr_no(rs.getString("mbr_no"));
				chkBrgRecord.setMbr_chk_date(rs.getString("mbr_chk_date"));
				chkBrgRecord.setMbr_chk_person(rs.getString("mbr_chk_person"));
				chkBrgRecord.setIs_evaluate(rs.getString("Is_evaluate"));
				list.add(chkBrgRecord);
			}
		} catch (SQLException e)
		{

		}
		dataOperation.close();
		return list;
	}
	public static void main(String[] args) {
/*		BridgeChk old_bc = new BridgeChk();
		//2017年沿海高速公路桥梁定期检查  prj_id 6e0f036d7b3e44fd89915562e9bf02be
		//2019年沿海高速定期检查  prj_id 8ba63281013842febc705a30ec0d2460
		//
		//2017 chk_id fa98ec5392b745bd91aef540e5399363
		//2018 chk_id 3d451fd9e2b843bea13b2dd7ed06b146
		
		old_bc.setChk_id("1ccfd83694ab4dcebb82f36c1e900982");
		//根据chk_id查出跨检查记录集合
		List<ChkSpanRecord> old_list = new CheckBridgeDao().getAllSpansData(old_bc).getSpans();
		for(ChkSpanRecord csr_old:old_list){
			String oldDirection = csr_old.getDirection();
			String oldSpanNo = csr_old.getSpan_no();
			String newChkId = "824b185044d643baa1281ed453210a6e";
			ChkSpanRecord newChkSpanRecord = new CheckBridgeDao().getChkSpanRecordByChkIdDirection(newChkId, oldDirection, oldSpanNo);
			//根据跨检查记录每条的 span_chk_id查出对应的构件检查记录 获取记录中的 mbr_chk_id
			 List<ChkBrgRecord> oldChkBrgRecords = new CheckBridgeDao().getChkBrgRecordBySpanChkId(csr_old.getSpan_chk_id());
			 //如果跨下面的构件检查记录集合不为空,查出对应该跨的18年项目构件检查记录集合.
			 if(oldChkBrgRecords.size()>0){
				 if(null==newChkSpanRecord){
					 newChkSpanRecord = new ChkSpanRecord();
					 String uuid = IDtool.getUUID();
					 newChkSpanRecord.setSpan_chk_id(uuid);
					 csr_old.setSpan_chk_id(uuid);
					 csr_old.setChk_id(newChkId);
					 CheckBridgeDao.getInstance().addSpan(csr_old);
				 }
				 //18年项目构件检查记录集合 chk_brg_record集合
				 List<ChkBrgRecord> newChkBrgRecords = new CheckBridgeDao().getChkBrgRecordBySpanChkId(newChkSpanRecord.getSpan_chk_id());	 
				 //如果老项目的构件检查记录有数据,而新项目里的构建检查记录无数据,将老项目的构件检查记录复制
				 if(newChkBrgRecords==null||newChkBrgRecords.size()==0){
					 //根据老项目的构件检查主键mbr_chk_id查出对应构件的所有病害以及病害中的图片photo
					 for(ChkBrgRecord old_chkBrgRecord:oldChkBrgRecords){
						 //新项目在chkBrgRecord中插入新纪录,生成一个新的
						 List<ChkBrgDefect> oldchkBrgDefects =  new CheckSpanDao().getDefectList(old_chkBrgRecord.getMbr_chk_id());
						 String newMbrChkId =IDtool.getUUID(); 
						 old_chkBrgRecord.setMbr_chk_id(newMbrChkId);
						 old_chkBrgRecord.setSpan_chk_id(newChkSpanRecord.getSpan_chk_id());
						 new CheckSpanDao().insertIntoChkBrgRecord(old_chkBrgRecord);
						 for(ChkBrgDefect oldChkBrgDefect:oldchkBrgDefects){
							 String newDefectSerial=IDtool.getUUID();
							 oldChkBrgDefect.setDefect_serial(newDefectSerial);
							 oldChkBrgDefect.setMbr_chk_id(newMbrChkId);
							 new CurrentControlDao().insertChkBrgDefect(oldChkBrgDefect);
							 List<ChkBrgPhoto> oldPhotoList = oldChkBrgDefect.getPhotos();
							 for(ChkBrgPhoto oldChkBrgPhoto:oldPhotoList){
								 oldChkBrgPhoto.setDefect_serial(newDefectSerial);
								 oldChkBrgPhoto.setPhoto_path("");
								 new CheckSpanDao().insertPhoto(oldChkBrgPhoto);
							 }
						 }
					 }
				 }
			 }
		}*/
		//deletePhotoPath();
		//根据新老项目进行缺少的检查记录恢复 @param 参数为老项目ID
		//改代码已更改为支持双库桥梁检查记录复制,把关键参数 "old"更改为 "new"即为单库复制
		//双库复制时,dbconfigCopy.properties 配置文件为被复制的库,即 old 库,   dbconfig.properties为新库,复制至的库,即 new 库
		/**
		 * @author Administrator
		 * @param 1:老项目ID
		 */
		List<BridgeChkVo> bridgeChkVos = getChkIdByPrjId("3802cbd13e554e1c801910083c2bd6e8","new");
		int i=1;
		for(BridgeChkVo bridgeChkVo:bridgeChkVos){
			System.out.println("第"+i+"座桥正在copy");
			String oldChkId = bridgeChkVo.getChkId();
			String brgId = bridgeChkVo.getBrgId();
			//--start  参数为新项目Id  加入该段代码后,即可解决遗新项目漏病害的问题
			String oldPrjId = getNearOldPrjId(brgId);
			       oldChkId = getNewChkIdByBrgIdAndPrjId(oldPrjId, brgId);
			//---end       
			       
			String newChkId = getNewChkIdByBrgIdAndPrjId("3802cbd13e554e1c801910083c2bd6e8", brgId);
			
			//oldChkId 老项目id，newChkId 新项目id，第三个参数，桥梁名称（日志用到，可以填空字符串）,第四个参数日志文件名称
			if(null!=newChkId&&!"".equals(newChkId)){
				copyByPrj(oldChkId, newChkId,bridgeChkVo.getBridgeName(),"2020年沿海高速盐通段部分桥梁专项检查");
				i++;
			}
		}
		
		
		//
		//1参数为 oldChkId,2参数为 newChkId
//		copyByPrj("2ee652efc58f475e8d04779d421f27c1", "f3f5baf9cbc54b028f5ff2da0536151a");
	}
	
	
	public static void copyByPrj(String oldChkId,String newChkId,String bridgeName,String fileName){
		Map map = new HashMap();
		String bridge_id = "";
		String span_no="";
		String direction="";
		BridgeChk old_bc = new BridgeChk();
		//2017年沿海高速公路桥梁定期检查  prj_id 6e0f036d7b3e44fd89915562e9bf02be
		//2019年沿海高速定期检查  prj_id 8ba63281013842febc705a30ec0d2460
		//
		//2017 chk_id fa98ec5392b745bd91aef540e5399363
		//2018 chk_id 3d451fd9e2b843bea13b2dd7ed06b146
		
		old_bc.setChk_id(oldChkId);
		//根据chk_id查出跨检查记录集合
		List<ChkSpanRecord> old_list = new CheckBridgeDao().getAllSpansData(old_bc,"old").getSpans();
		for(ChkSpanRecord csr_old:old_list){
			String oldDirection = csr_old.getDirection();
			String oldSpanNo = csr_old.
					getSpan_no();
			//String newChkId = "824b185044d643baa1281ed453210a6e";
			ChkSpanRecord newChkSpanRecord = new CheckBridgeDao().getChkSpanRecordByChkIdDirection(newChkId, oldDirection, oldSpanNo);
			//根据跨检查记录每条的 span_chk_id查出对应的构件检查记录 获取记录中的 mbr_chk_id
			 List<ChkBrgRecord> oldChkBrgRecords = new CheckBridgeDao().getChkBrgRecordBySpanChkId(csr_old.getSpan_chk_id(),"old");
			 //如果跨下面的构件检查记录集合不为空,查出对应该跨的18年项目构件检查记录集合.
			 if(oldChkBrgRecords.size()>=0){
				 if(null==newChkSpanRecord){
					 newChkSpanRecord = new ChkSpanRecord();
					 String uuid = IDtool.getUUID();
					 newChkSpanRecord.setSpan_chk_id(uuid);
					 csr_old.setSpan_chk_id(uuid);
					 csr_old.setChk_id(newChkId);
					 csr_old.setChk_state("0");
					 CheckBridgeDao.getInstance().addSpan(csr_old);
				 }
				 //18年项目构件检查记录集合 chk_brg_record集合
				 List<ChkBrgRecord> newChkBrgRecords = new CheckBridgeDao().getChkBrgRecordBySpanChkId(newChkSpanRecord.getSpan_chk_id(),"new");	 
				 //如果老项目的构件检查记录有数据,而新项目里的构建检查记录无数据,将老项目的构件检查记录复制
				 try {
					 //  改行判断为等于null时为 新检查记录里没有是从老项目复制
					//if(newChkBrgRecords==null||newChkBrgRecords.size()==0){
					//  改行判断为等于null时为 新检查记录里没有是从老项目复制
					if(true){
						
						 //根据老项目的构件检查主键mbr_chk_id查出对应构件的所有病害以及病害中的图片photo
						 for(ChkBrgRecord old_chkBrgRecord:oldChkBrgRecords){
							 
							 String old_mbr_no = old_chkBrgRecord.getMbr_no();
							 boolean flag = volidateMbrNo(old_mbr_no,newChkBrgRecords);
							 //if("2".equals(old_chkBrgRecord.getSpan_no())) {
								 //map.put(old_chkBrgRecord.getBridge_id(), 1);
//								 System.out.println("****"+old_chkBrgRecord.getBridge_id()+"******");
							 //}
							 if(!flag) {
								 bridge_id = old_chkBrgRecord.getBridge_id();
								 span_no=old_chkBrgRecord.getSpan_no();
								 direction=old_chkBrgRecord.getDirection();
								 if(!"".equals(bridge_id)) {
									 FileUtils.WriteNewLog(bridge_id+" , "+bridgeName+" , "+direction+" , "+span_no+" , "+old_chkBrgRecord.getMbr_type(), "E:\\Download",fileName);
								 }
								 //新项目在chkBrgRecord中插入新纪录,生成一个新的
								 List<ChkBrgDefect> oldchkBrgDefects =  new CheckSpanDao().getDefectList(old_chkBrgRecord.getMbr_chk_id(),"old");
								 String newMbrChkId =IDtool.getUUID(); 
								 old_chkBrgRecord.setMbr_chk_id(newMbrChkId);
								 old_chkBrgRecord.setSpan_chk_id(newChkSpanRecord.getSpan_chk_id());
								 new CheckSpanDao().insertIntoChkBrgRecord(old_chkBrgRecord);
								 for(ChkBrgDefect oldChkBrgDefect:oldchkBrgDefects){
									 String newDefectSerial=IDtool.getUUID();
									 oldChkBrgDefect.setDefect_serial(newDefectSerial);
									 oldChkBrgDefect.setMbr_chk_id(newMbrChkId);
									 new CurrentControlDao().insertChkBrgDefect(oldChkBrgDefect);
									 List<ChkBrgPhoto> oldPhotoList = oldChkBrgDefect.getPhotos();
									 StringBuffer str = new StringBuffer("");
									 for(ChkBrgPhoto oldChkBrgPhoto:oldPhotoList){
										 oldChkBrgPhoto.setDefect_serial(newDefectSerial);
										 //是否需要携带图片
										 //oldChkBrgPhoto.setPhoto_path("");
										 
										/*
										 * if(!str.toString().contains(oldChkBrgPhoto.getPhoto_path())) {
										 * str.append(oldChkBrgPhoto.getPhoto_path()); new
										 * CheckSpanDao().insertPhoto(oldChkBrgPhoto); }
										 */
										 
									 }
								 }
							 }
						 }
					 }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		}
		
		System.out.println(bridge_id);
		
	}
	
	
	public static void deletePhotoPath(){
		BridgeChk old_bc = new BridgeChk();
		//2017年太仓港北疏港高速定期检测  prj_id fb784207b83e41c88469209429f4f0b3
		//2018年太仓港北疏港高速定期检测  prj_id c9e391e4a65a4296a491c05e9f3f7df1
		//fa98ec5392b745bd91aef540e5399363
		//2017 chk_id fa98ec5392b745bd91aef540e5399363
		//2018 chk_id 3d451fd9e2b843bea13b2dd7ed06b146
		old_bc.setChk_id("824b185044d643baa1281ed453210a6e");
		//根据chk_id查出跨检查记录集合
		List<ChkSpanRecord> old_list = new CheckBridgeDao().getAllSpansData(old_bc,"old").getSpans();
		for(ChkSpanRecord csr_old:old_list){
			String oldDirection = csr_old.getDirection();
			String oldSpanNo = csr_old.getSpan_no();
			String newChkId = "824b185044d643baa1281ed453210a6e";
			ChkSpanRecord newChkSpanRecord = new CheckBridgeDao().getChkSpanRecordByChkIdDirection(newChkId, oldDirection, oldSpanNo);
			//根据跨检查记录每条的 span_chk_id查出对应的构件检查记录 获取记录中的 mbr_chk_id
			 List<ChkBrgRecord> oldChkBrgRecords = new CheckBridgeDao().getChkBrgRecordBySpanChkId(csr_old.getSpan_chk_id(),"old");
			 //如果跨下面的构件检查记录集合不为空,查出对应该跨的18年项目构件检查记录集合.
			 if(oldChkBrgRecords.size()>0){
				 //18年项目构件检查记录集合 chk_brg_record集合
				 List<ChkBrgRecord> newChkBrgRecords = new CheckBridgeDao().getChkBrgRecordBySpanChkId(newChkSpanRecord.getSpan_chk_id(),"old");	 
				 //如果老项目的构件检查记录有数据,而新项目里的构建检查记录无数据,将老项目的构件检查记录复制
				 if(newChkBrgRecords!=null&&newChkBrgRecords.size()!=0){
					 //根据老项目的构件检查主键mbr_chk_id查出对应构件的所有病害以及病害中的图片photo
					 for(ChkBrgRecord old_chkBrgRecord:oldChkBrgRecords){
						 //新项目在chkBrgRecord中插入新纪录,生成一个新的
						 List<ChkBrgDefect> oldchkBrgDefects =  new CheckSpanDao().getDefectList(old_chkBrgRecord.getMbr_chk_id());
						 for(ChkBrgDefect oldChkBrgDefect:oldchkBrgDefects){
							 String newDefectSerial=IDtool.getUUID();
							 oldChkBrgDefect.setDefect_serial(newDefectSerial);
							 List<ChkBrgPhoto> oldPhotoList = oldChkBrgDefect.getPhotos();
							 for(ChkBrgPhoto oldChkBrgPhoto:oldPhotoList){
								 oldChkBrgPhoto.setDefect_serial(newDefectSerial);
								 oldChkBrgPhoto.setPhoto_path("");
								 new CheckSpanDao().updatePhotoPath("",oldChkBrgPhoto.getPhoto_id());
							 }
						 }
					 }
				 }
			 }
		}
	}
	
	
	public static boolean volidateMbrNo(String old_mbr_no,List<ChkBrgRecord> newChkBrgRecords) {
		boolean flag = false;
		for (ChkBrgRecord chkBrgRecord : newChkBrgRecords) {
			if(old_mbr_no.equals(chkBrgRecord.getMbr_no())) {
				flag = true;
			}
		}
		return flag;
		
	}
}
