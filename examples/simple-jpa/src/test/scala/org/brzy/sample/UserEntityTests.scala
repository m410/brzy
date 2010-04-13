package org.brzy.sample

import javax.persistence.{Persistence, EntityManagerFactory}
import org.junit.Test
import org.junit.Assert._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

class UserEntityTests {

  val factory:EntityManagerFactory = Persistence.createEntityManagerFactory("monetizeit-test")

    @Test
    def testGetUser {
        val entityManager = factory.createEntityManager()
        entityManager.getTransaction().begin()
    
//        def user = User.get(1)

        entityManager.getTransaction().commit()
        entityManager.close()
        assertTrue(true)
    }
}