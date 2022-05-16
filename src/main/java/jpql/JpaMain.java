package jpql;

import com.sun.org.apache.xpath.internal.operations.Or;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setMemberType(MemberType.USER);

            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

//            String query = "select  "+
//                    "case when m.age <= 10 then '학생요금'" +
//                    "when m.age >= 60 then '경로요금'"+
//                    "else '일반요금'"+
//                    " end"+
//                    " from Member m";

            String query = "select coalesce(m.username, '이름이 없는 회원') from Member m";
            List<Object[]> result = em.createQuery(query)
                    .getResultList();

            for (Object[] objects : result){
                System.out.println("objects : "+objects[0]);
                System.out.println("objects : "+objects[1]);
                System.out.println("objects : "+objects[2]);
            }

//            List<Member> result = em.createQuery("select m from Member m order by m.age desc", Member.class)
//                    .setFirstResult(0)
//                    .setMaxResults(10)
//                    .getResultList();
//
//            for (Member member1 : result){
//                System.out.println(member1.toString());
//            }

//            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);// 반환 타입 명확 O
            //            Query query1 = em.createQuery("select m.username,m.age from Member m"); // 반환 타입 명확 X
//            List<Member> memberList = query.getResultList();
//            Member singleResult = query.getSingleResult();
            /*
             * getResultList의 결과가 없을 땐 빈 리스트를 반환
             *
             * getSingleResult 결과가 없을때는 nullpoint;; 둘 이상일때는 non unique 에러가 발생
             *            * */
//            List<MemberDto> resultList = em.createQuery("select new jpql.MemberDto(m.username,m.age) from Member m", MemberDto.class)
//                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close(); // 반드시 닫아줘야함. 그래야 내부적으로 DB Connection 을 반환한다.
        }
        emf.close();
    }
}
