package gym

import data.EBoss
import data.EType
import org.junit.Assert.*
import org.junit.Test

class GymLeaderBattleCalculatorTest{

    val calculator = GymLeaderBattleCalculator()

    @Test
    fun testBattle() {
        val bestTeam = calculator.calculateBestTeamForGymLeader(EBoss.SURGE_2, EType.POISON)
        println(bestTeam)
    }
}