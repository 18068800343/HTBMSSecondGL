package hs.bm.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import hs.bm.bean.EvaBridgePart;
import hs.bm.bean.Unitevaluationrec;
import hs.bm.vo.Eval11Info;
import hs.bm.vo.IndexsetIndexVO;
import hs.bm.vo.OperationConstruct;

public class Eval11Dao {

	private static Eval11Dao eval11Dao;

	public static Eval11Dao getInstance() {
		if (eval11Dao == null) {
			eval11Dao = new Eval11Dao();
		}
		return eval11Dao;
	}

	public Map<String, Object> initTable(OperationConstruct oc) {
		Map<String, Object> map = new HashMap<String, Object>();
		 List<Eval11Info> ll = new ArrayList<Eval11Info>();
		 String sql = "select * from eva_brg_rec where prj_id=? and bridge_id=? and ER_STD='2011'";
		 String er_no = "";
		 String audit_state = "";
		 MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		 ResultSet rs = null;
		 try {
			rs = dataOperation.executeQuery(sql, new String[]{oc.getPrj_id(), oc.getId()});
			while(rs.next()){
				er_no = rs.getString("er_no");
				audit_state = rs.getString("audit_state");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sql = "select a.*,b.*,c.brg_type_id,c.s_id,d.*,e.eva_mbr_calcu_value  from (SELECT * from chk_brg_record where span_chk_id in (select span_chk_id from chk_span_record where chk_id=?)) as a  LEFT JOIN dic_brg_struct_member_def AS b on a.mbr_type = b.member_name LEFT JOIN brg_span_info AS c ON c.direction = a.direction AND c.span_no = a.span_no AND c.bridge_id = a.bridge_id LEFT JOIN brg_mbr_info AS d ON d.r_id = a.mbr_no LEFT JOIN eva_mbr_calcu AS e ON a.mbr_no = e.member_no and e.prj_id=? ";
		 rs = dataOperation.executeQuery(sql, new String[]{oc.getChk_id(),oc.getPrj_id()});
		 try {
				while(rs.next()){
					Eval11Info ev = new Eval11Info();
					ev.setAudit_state(audit_state);
					ev.setState(rs.getString("Is_evaluate"));
					ev.setEr_std("2011");
					ev.setEr_no(er_no);
					ev.setBridge_id(oc.getId());
					ev.setPrj_id(oc.getPrj_id());
					ev.setDirection(rs.getString("direction"));
					ev.setSpan_no(rs.getString("span_no"));
					ev.setMember_type(rs.getString("mbr_type"));
					ev.setMember_no(rs.getString("member_no"));
					ev.setMbr_no(rs.getString("mbr_no"));
					ev.setDistr_name(rs.getString("distr_name"));
					String brg_type_id=rs.getString("brg_type_id");
					String member_id = rs.getString("member_id");
					String mbr_chk_id = rs.getString("mbr_chk_id");
					ev.setMbr_chk_id(mbr_chk_id);
					String s_id = rs.getString("s_id");
					ev.setBrg_type_id(brg_type_id);
					ev.setMember_id(member_id);
					ev.setS_id(s_id);
					ev.setCalcu_value(rs.getString("eva_mbr_calcu_value"));
					if(brg_type_id.equals("1")||brg_type_id.equals("2")
		    			     ||brg_type_id.equals("3")||brg_type_id.equals("4")
		    			     ||brg_type_id.equals("5")||brg_type_id.equals("6")
		    			     ||brg_type_id.equals("7")||brg_type_id.equals("8")
		    			     ||brg_type_id.equals("9")||brg_type_id.equals("10")
		    			     ||brg_type_id.equals("11")||brg_type_id.equals("12")
		    			     ||brg_type_id.equals("13")){
		    				ev.setComponent_name(rs.getString("component1"));
		    			}else if(brg_type_id.equals("14")||brg_type_id.equals("15")
		    					||brg_type_id.equals("16")||brg_type_id.equals("17")
		    					||brg_type_id.equals("18")||brg_type_id.equals("23")
		    					||brg_type_id.equals("24")||brg_type_id.equals("25")
		    					||brg_type_id.equals("26")||brg_type_id.equals("27")){
		    				ev.setComponent_name(rs.getString("component2"));
		    			}else if(brg_type_id.equals("19")||brg_type_id.equals("20")
		    					||brg_type_id.equals("28")||brg_type_id.equals("29")){
		    				ev.setComponent_name(rs.getString("component3"));
		    			}else if(brg_type_id.equals("21")||brg_type_id.equals("22")
		    					||brg_type_id.equals("30")||brg_type_id.equals("31")){
		    				ev.setComponent_name(rs.getString("component4"));
		    			}else if(brg_type_id.equals("32")||brg_type_id.equals("33")||brg_type_id.equals("34")){
		    				ev.setComponent_name(rs.getString("component5"));
		    			}else if(brg_type_id.equals("35")||brg_type_id.equals("36")||brg_type_id.equals("37")){
		    				ev.setComponent_name(rs.getString("component6"));
		    			}
					if(ev.getComponent_name()!=null&&!ev.getComponent_name().equals("")){
						ll.add(ev);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		 map.put("ll", ll);
		 map.put("er_no", er_no);
		 map.put("audit_state", audit_state);
		 dataOperation.close();
		 return map;
	}
	
	public List<IndexsetIndexVO> getIndex(Eval11Info ev){
		List<IndexsetIndexVO> ll = new ArrayList<IndexsetIndexVO>();
		String sql ="select * from indexset_recognise_info where brg_base_type in ('全部',(select brg_type_name from dic_brg_struct_type_def where brg_type_id = ?)) and member_id=? and member_type_name = (select member_model from brg_mbr_info where s_id=? and member_no=?)";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs=dataOperation.executeQuery(sql, new Object[]{ev.getBrg_type_id(), ev.getMember_id(), ev.getS_id(), ev.getMember_no()});
		String indexset_id = null;
		try {
			if(rs.next()){
				indexset_id = rs.getString("indexset_id");
				System.out.println(indexset_id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(indexset_id==null){
			ll=null;
		}else{
			ll = queryIndex(indexset_id,ev);
		}
		dataOperation.close();
		return ll;
	}
	
	
	/**
	 * 查询指标和所对应的指标值
	 * @param obj
	 * @return
	 */
	public List<IndexsetIndexVO> queryIndex(String indexset_id,Eval11Info ev){
		List<IndexsetIndexVO> ll=new ArrayList<IndexsetIndexVO>();
		String sql="select ii.*,em.value from (select *  from indexset_index where indexset_id=? )ii left JOIN eva_mbr em on   em.mbr_chk_id=? and ii.index_id=em.index_id GROUP BY index_id";
		Connection conn=MyDataSource.getInstance().getConnection();
    	MyDataOperation mdo=new MyDataOperation(conn,false);
    	ResultSet rs=mdo.executeQuery(sql, new String[]{indexset_id, ev.getMbr_chk_id()});
		try{
			while(rs.next()){
				IndexsetIndexVO iv=new IndexsetIndexVO();
				iv.setIndex_id(rs.getString("index_id"));
				iv.setIndex_name(rs.getString("index_name"));
				iv.setIndex_scale(rs.getString("index_scale"));
				iv.setIndexset_id(rs.getString("indexset_id"));
				String value = "1";
				if(rs.getString("value")==null){
					int i=0;
					String uuid=UUID.randomUUID().toString();
					i=this.insertValue(new Object[]{uuid,ev.getPrj_id(),ev.getBridge_id(),ev.getSpan_no(),ev.getComponent_name(),ev.getMember_type(),ev.getMbr_no(),iv.getIndex_id(),ev.getDirection(),ev.getDistr_name(),1,ev.getMbr_chk_id()});
					if(i==-1){
						mdo.close();
						return null;
					}
				}else{
					value=rs.getString("value");
				}
				iv.setValue(value);
				ll.add(iv);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			mdo.close();
		}
		return ll;
	}

	/**
	 * 插入指标所对应的值
	 * @param obj
	 * @return
	 */
	public int insertValue(Object[] obj){
		
		int i=0;
		String sql="insert into eva_mbr(mbr_no_code,prj_id,bridge_id,span_no,component_name,member_name,member_no,index_id,bridge_direction,bridge_part,value,mbr_chk_id) values(?,?,?,?,?,?,?,?,?,?,?,?)";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		i = dataOperation.executeUpdate(sql, obj);
		dataOperation.close();
		return i;
	}

	public int saveIndex(Eval11Info ev, List<IndexsetIndexVO> ll) {
		String sql = "update eva_mbr set value=? where prj_id=? and bridge_id=? and mbr_chk_id=? and index_id=?";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		int k =0;
		for(int i=0;i<ll.size();i++){
			IndexsetIndexVO st = ll.get(i);
			k= dataOperation.executeUpdate(sql, new String[]{
					st.getValue(),
					ev.getPrj_id(),
					ev.getBridge_id(),
					ev.getMbr_chk_id(),
					st.getIndex_id()
			});
		}
		sql = "update chk_brg_record set Is_evaluate='1' where mbr_chk_id=?";
		k = dataOperation.executeUpdate(sql, new String[]{ev.getMbr_chk_id()});
		dataOperation.close();
		return k;
	}

	public int overEval(String id, String prj_id) {
		String sql = "update eva_brg_rec set audit_state='1',er_date=?,pdf=null where bridge_id=? and prj_id=? and er_std='2011'";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		int i = dataOperation.executeUpdate(sql, new String[]{
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
				id,
				prj_id
		});
		dataOperation.close();
		return i;
	}

	public Map<String, Map<String, EvaBridgePart>> getEvaRes(String prj_id, String id) {
		String sql = "select a.*,b.* from eva_bridge_part a,dic_brg_struct_type_def b where bridge_no=? and prj_no=? and a.bridge_type=b.brg_type_id;";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{ id, prj_id });
		Map<String, Map<String, EvaBridgePart>> map = new HashMap<String, Map<String, EvaBridgePart>>();
		try {
			while(rs.next()){
				EvaBridgePart evaBridgePart = new EvaBridgePart();
				evaBridgePart.setBridge_direction(rs.getString("bridge_direction"));
				evaBridgePart.setBridge_spans(rs.getString("bridge_spans"));
				evaBridgePart.setEva_bridge_part_value(rs.getString("eva_bridge_part_value"));
				evaBridgePart.setEva_bridge_part_value1(rs.getString("eva_bridge_part_value1"));
				evaBridgePart.setEva_bridge_part_value2(rs.getString("eva_bridge_part_value2"));
				evaBridgePart.setEva_bridge_part_value3(rs.getString("eva_bridge_part_value3"));
				evaBridgePart.setBridge_type_name(rs.getString("brg_type_name"));
				
				if(!map.containsKey(evaBridgePart.getBridge_type_name())){
					map.put(evaBridgePart.getBridge_type_name(), new HashMap<String, EvaBridgePart>());
				}
				map.get(evaBridgePart.getBridge_type_name()).put(evaBridgePart.getBridge_direction(), evaBridgePart);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return map;
	}
	
	public List<EvaBridgePart> getpinfen11EvaBridgePart(String prj_id, String bridge_id) {
		String sql = "select a.eva_bridge_part_value1,a.eva_bridge_part_value2,a.eva_bridge_part_value3, " + 
				"       a.eva_bridge_part_index1,a.eva_bridge_part_index2,a.eva_bridge_part_index3, " + 
				"	   b.er_grade,b.er_level,a.bridge_direction " + 
				"from eva_bridge_part as a " + 
				"left join evaluationrec as b on a.bridge_no=b.bridge_no and a.prj_no=b.prj_no " + 
				"where b.er_std='2011' and a.prj_no=? and a.bridge_no=?;";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id,bridge_id});
		List<EvaBridgePart> list = new ArrayList<EvaBridgePart>();
		EvaBridgePart evaBridgePart;
		try {
			while(rs.next()){
				evaBridgePart = new EvaBridgePart();
				evaBridgePart.setEva_bridge_part_value1(rs.getString("eva_bridge_part_value1"));
				evaBridgePart.setEva_bridge_part_value2(rs.getString("eva_bridge_part_value2"));
				evaBridgePart.setEva_bridge_part_value3(rs.getString("eva_bridge_part_value3"));
				evaBridgePart.setEva_bridge_part_index1(rs.getByte("eva_bridge_part_index1"));
				evaBridgePart.setEva_bridge_part_index2(rs.getByte("eva_bridge_part_index2"));
				evaBridgePart.setEva_bridge_part_index3(rs.getByte("eva_bridge_part_index3"));
				evaBridgePart.setEva_bridge_part_value(rs.getString("er_grade"));
				evaBridgePart.setEva_bridge_part_grde(rs.getString("er_level"));
				evaBridgePart.setBridge_direction(rs.getString("bridge_direction"));
				list.add(evaBridgePart);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		if(list.size()!=0&&list.get(0).getBridge_direction().equals("无"))
		{
			double pf1 = 0,pf2=0,pf3=0;
			int num = list.size();
			for(int i=0;i<list.size();i++)
			{
				pf1+=Double.parseDouble(list.get(i).getEva_bridge_part_value1());
				pf2+=Double.parseDouble(list.get(i).getEva_bridge_part_value2());
				pf3+=Double.parseDouble(list.get(i).getEva_bridge_part_value3());
			}
			evaBridgePart = new EvaBridgePart();
			evaBridgePart.setEva_bridge_part_value1(String.format("%.4f", pf1/num));
			evaBridgePart.setEva_bridge_part_value2(String.format("%.4f", pf2/num));
			evaBridgePart.setEva_bridge_part_value3(String.format("%.4f", pf3/num));
			evaBridgePart.setEva_bridge_part_index1(list.get(list.size()-1).getEva_bridge_part_index1());
			evaBridgePart.setEva_bridge_part_index2(list.get(list.size()-1).getEva_bridge_part_index2());
			evaBridgePart.setEva_bridge_part_index3(list.get(list.size()-1).getEva_bridge_part_index3());
			evaBridgePart.setEva_bridge_part_value(list.get(list.size()-1).getEva_bridge_part_value());
			evaBridgePart.setEva_bridge_part_grde(list.get(list.size()-1).getEva_bridge_part_grde());
			evaBridgePart.setBridge_direction(list.get(list.size()-1).getBridge_direction());
			list.clear();
			list.add(evaBridgePart);
		}else if(list.size()!=0){
			double pf1 = 0,pf2=0,pf3=0,pf4=0,pf5=0,pf6=0;
			int count1=0,count2=0;
			EvaBridgePart evaBridgePart1 = new EvaBridgePart();
			EvaBridgePart evaBridgePart2 = new EvaBridgePart();
			for(int i=0;i<list.size();i++)
			{
				if(list.get(i).getBridge_direction().equals("上行")) {
					pf1+=Double.parseDouble(list.get(i).getEva_bridge_part_value1());
					pf2+=Double.parseDouble(list.get(i).getEva_bridge_part_value2());
					pf3+=Double.parseDouble(list.get(i).getEva_bridge_part_value3());
					evaBridgePart1.setEva_bridge_part_index1(list.get(i).getEva_bridge_part_index1());
					evaBridgePart1.setEva_bridge_part_index2(list.get(i).getEva_bridge_part_index2());
					evaBridgePart1.setEva_bridge_part_index3(list.get(i).getEva_bridge_part_index3());
					evaBridgePart1.setEva_bridge_part_value(list.get(i).getEva_bridge_part_value());
					evaBridgePart1.setEva_bridge_part_grde(list.get(i).getEva_bridge_part_grde());
					evaBridgePart1.setBridge_direction(list.get(i).getBridge_direction());
					count1++;
				}else {
					pf4+=Double.parseDouble(list.get(i).getEva_bridge_part_value1());
					pf5+=Double.parseDouble(list.get(i).getEva_bridge_part_value2());
					pf6+=Double.parseDouble(list.get(i).getEva_bridge_part_value3());
					evaBridgePart2.setEva_bridge_part_index1(list.get(i).getEva_bridge_part_index1());
					evaBridgePart2.setEva_bridge_part_index2(list.get(i).getEva_bridge_part_index2());
					evaBridgePart2.setEva_bridge_part_index3(list.get(i).getEva_bridge_part_index3());
					evaBridgePart2.setEva_bridge_part_value(list.get(i).getEva_bridge_part_value());
					evaBridgePart2.setEva_bridge_part_grde(list.get(i).getEva_bridge_part_grde());
					evaBridgePart2.setBridge_direction(list.get(i).getBridge_direction());
					count2++;
					}
					
					
				}
				evaBridgePart1.setEva_bridge_part_value1(String.format("%.4f", pf1/count1));
				evaBridgePart1.setEva_bridge_part_value2(String.format("%.4f", pf2/count1));
				evaBridgePart1.setEva_bridge_part_value3(String.format("%.4f", pf3/count1));
				evaBridgePart2.setEva_bridge_part_value1(String.format("%.4f", pf4/count2));
				evaBridgePart2.setEva_bridge_part_value2(String.format("%.4f", pf5/count2));
				evaBridgePart2.setEva_bridge_part_value3(String.format("%.4f", pf6/count2));
				list.clear();
				list.add(evaBridgePart1);
				list.add(evaBridgePart2);
			
		}
		return list;
	}
	
	public List<Unitevaluationrec> getpinfen11Unitevaluationrec(String prj_id, String bridge_id) {
		String sql = "select a.component_id,a.eva_ubr_part,a.uer_grade,a.uer_index,b.bridge_direction " + 
				"from unitevaluationrec as a " + 
				"inner join eva_mbr_calcu as b on a.prj_no=b.prj_id and a.bridge_no=b.bridge_id and a.component_id=b.component_id and a.bridge_direction=b.bridge_direction " + 
				"where a.prj_no like ? and a.bridge_no like ? " + 
				"group by a.component_id,b.bridge_direction;";
		
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id,bridge_id});
		List<Unitevaluationrec> utrList = new ArrayList<Unitevaluationrec>();
		try {
			while(rs.next()){
				Unitevaluationrec utr = new Unitevaluationrec();
				utr.setEva_ubr_part(rs.getString("eva_ubr_part"));
				utr.setComponent_id(rs.getString("component_id"));
				utr.setUer_grade(rs.getFloat("uer_grade"));
				utr.setUer_index(rs.getInt("uer_index"));
				utr.setBridge_direction(rs.getString("bridge_direction"));
				utrList.add(utr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sql2 = "select dbsm.component1,count(dbsm.component1) as counts,bsi.direction "+
				"from brg_span_info as bsi "+
				"inner join brg_mbr_info as bmi on bsi.s_id=bmi.s_id "+
				"inner join dic_brg_struct_member_def as dbsm on bmi.member_type=dbsm.member_name "+
				"where bsi.bridge_id like ? "+
				"group by dbsm.component1,bsi.direction ";
		MyDataOperation dataOperation2 = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs2 = dataOperation2.executeQuery(sql2, new String[]{bridge_id});
		
		try {
			while(rs2.next()){
				String component_id=rs2.getString("component1");
				int counts = rs2.getInt("counts");
				String direction=rs2.getString("direction");
				for(Unitevaluationrec u : utrList)
				{
					if(u!=null&&u.getComponent_id()!=null&&u.getComponent_id().equals(component_id)&&u.getBridge_direction().equals(direction))
					{
						u.setUer_unitid(counts);
					}
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		dataOperation2.close();
		return utrList;
	}

	public boolean checkPrjEvaCount(String id, String prj_id){
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		String sql = "select * from eva_brg_rec where bridge_id=? and prj_id=?";
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{ id, prj_id});
		boolean flag = false;
		int f1 = 0;
		int f2 = 0;
		try {
			while(rs.next()){
				int f =  rs.getInt("audit_state");
				String  std = rs.getString("er_std");
				if (std.equals("2004")) {
					f1 = f;
				}
				if(std.equals("2011")){
					f2 = f;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		
		if(f1!=0 && f2!=0){
			flag = true;
		}else{
			if(f1==0){
				flag = true;
			}
		}
		return flag;
	}
	
	public void changePrjEvaCount(String prj_id){
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		String sql = "  update chk_project_info set struct_eva=struct_eva+1 WHERE prj_id = ? ";
		dataOperation.executeUpdate(sql, new String[]{ prj_id });
		dataOperation.close();
	}
	
	
	
	
	
	public Map<String, String> getScore(String prj_id, String bridge_id){
		Map<String, String> map = new HashMap<>();
		String sql = "select * from eva_brg_rec where prj_id=? and bridge_id=? and er_std='2011'";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		ResultSet rs = dataOperation.executeQuery(sql, new String[]{prj_id, bridge_id});
		try {
			while(rs.next()){
				map.put("score_clean", rs.getString("score_clean"));
				map.put("score_fix", rs.getString("score_fix"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dataOperation.close();
		return map;
	}
	public int setScore(String score_clean, String score_fix, String prj_id, String bridge_id){
		String sql = "update eva_brg_rec set score_clean=?,score_fix=? where prj_id=? and bridge_id=? and er_std='2011'";
		MyDataOperation dataOperation = new MyDataOperation(MyDataSource.getInstance().getConnection());
		int i = dataOperation.executeUpdate(sql, new String[]{score_clean, score_fix, prj_id, bridge_id});
		dataOperation.close();
		return i;
	}
}
