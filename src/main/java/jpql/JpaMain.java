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
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.setMemberType(MemberType.USER);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(10);
            member2.setMemberType(MemberType.USER);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(10);
            member3.setMemberType(MemberType.USER);

            member1.setTeam(teamA);
            member2.setTeam(teamA);
            member3.setTeam(teamB);
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            em.flush();
            em.clear();

//            String query = "select distinct t from Team t";
//            List<Team> teamList = em.createQuery(query,Team.class)
//                    .getResultList();
//
//            System.out.println(teamList.size());
//
//            for (Team team : teamList) {
//                System.out.println("team = " + team.getName() + "|members = "+team.getMembers().size());
//                for (Member member : team.getMembers()) {
//                    System.out.println("-> member = "+ member);
//                }
//            }

//            Member findMember = em.createNamedQuery("Member.findByUsername", Member.class)
//                    .setParameter("username", member3.getUsername())
//                    .getSingleResult();
//
//            System.out.println("findMember => "+findMember);

            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            System.out.println("resultCount = " + resultCount);

            /*
            * 벌크연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
            *
            * 벌크 연산을 먼저 실행
            * 벌크 연산을 수행 후 영속성 컨텍스트 초기화
            * */

            /*
            * fetch join VS join
            *
            * 일반 조인 실행 시 연관된 엔티티를 함께 조회하지 않음.
            *
            * JPQL은 결과를 반환할 깨 연관관계 고려X
            * 단지 select 절에 지정한 엔티티만 조회할 뿐
            * 여기서는 team 엔티티만 조회하고, 회원 엔티티는 조회 X
            *
            * 페치조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)
            * 페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념
            *
            * 페치 조인의 한계
            *
            * 페치 조인 대상에는 별칭을 줄 수 없다.
            * 둘 이상의 컬렉션은 페치조인 할 수 없다.
            * 컬렉션을 페치조인하면 페이징 API를 사용할 수 없다.
            *   일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능
            *   하이버네이트는 경고 로그를 남기고 메모리에서 페이징
            *
            * 특징
            *
            * 얀관된 엔티티들을 SQL 한 번으로 조회 - 성능최적화
            * 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
            * 실무에서 글로벌 로딩 전략은 모두 지연 로딩
            * 최적화가 필요한 곳은 페치조인 적용
용           **/

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

//            String query = "select m from Team t join t.members m";
//            Integer result = em.createQuery(query,Integer.class)
//                    .getSingleResult();
//
//            System.out.println("result => "+ result);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close(); // 반드시 닫아줘야함. 그래야 내부적으로 DB Connection 을 반환한다.
        }
        emf.close();
    }
}
