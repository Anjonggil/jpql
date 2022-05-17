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

//            String query = "select coalesce(m.username, '이름이 없는 회원') from Member m";
//            List<Object[]> result = em.createQuery(query)
//                    .getResultList();

//            for (Object[] objects : result){
//                System.out.println("objects : "+objects[0]);
//                System.out.println("objects : "+objects[1]);
//                System.out.println("objects : "+objects[2]);
//            }

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

            /*
            * 경로 표현식
            * 상태 필드 : 경로 탐색의 끝. 탐색 X
            * 단일 값 연관 경로 : 묵시적 내부 조인 발생. 탐색 O
            * 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색 X
            * FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
            *
            * 실무에서는 묵시적 조인을 사용하지 말자 !!\
            * 조인은 SQL 튜닝에 중요 포인트
            * 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어렵다.
            *
            * */

            String query = "select m from Team t join t.members m";
            Integer result = em.createQuery(query,Integer.class)
                    .getSingleResult();

            System.out.println("result => "+ result);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close(); // 반드시 닫아줘야함. 그래야 내부적으로 DB Connection 을 반환한다.
        }
        emf.close();
    }
}
