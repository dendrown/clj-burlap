(ns gridworld.core
  (:import (burlap.behavior.singleagent.auxiliary.performance LearningAlgorithmExperimenter
                                                              PerformanceMetric
                                                              TrialMode)
           (burlap.behavior.singleagent.learning LearningAgentFactory)
           (burlap.behavior.singleagent.learning.tdmethods QLearning)
           (burlap.domain.singleagent.gridworld GridWorldDomain
                                                GridWorldVisualizer)
           (burlap.domain.singleagent.gridworld.state GridAgent
                                                      GridLocation
                                                      GridWorldState)
           (burlap.mdp.auxiliary.common ConstantStateGenerator
                                        SinglePFTF)
           (burlap.mdp.auxiliary.stateconditiontest TFGoalCondition)
           (burlap.mdp.core.oo.propositional PropositionalFunction)
           (burlap.mdp.singleagent.common GoalBasedRF)
           (burlap.mdp.singleagent.environment SimulatedEnvironment)
           (burlap.shell.visual VisualExplorer)
           (burlap.statehashing.simple SimpleHashableStateFactory))
  (:gen-class))

(def ^:const Size-X 11)
(def ^:const Size-Y 11)


;;; --------------------------------------------------------------------------
(defn- init-world
  "Returns an 11x11, 4-room grid world."
  []
  (doto (GridWorldDomain. Size-X Size-Y)            ; 11x11 grid world
        (.setMapToFourRooms)                        ; four-room layout
        (.setProbSucceedTransitionDynamics 0.8)))   ; stochastic transition success rate



;;; --------------------------------------------------------------------------
(defn- init-state
  "Returns an initialized grid world state."
  []
  (GridWorldState. (GridAgent. 0 0)                 ; Bottom-Left
                   [(GridLocation. (dec Size-X)      ; Top-Right
                                   (dec Size-Y)
                                   "loc0")]))



;;; --------------------------------------------------------------------------
(defn hello
  "Launches a GUI for the specified grid world."
  [gw]
  (let [domain (.generateDomain gw)                         ; generate the grid world domain
        exp (VisualExplorer. domain
                             (GridWorldVisualizer/getVisualizer (.getMap gw))
                             (init-state))]

    ;; set control keys to use w-s-a-d
    (run! (fn [[code dir]]
            (.addKeyAction exp code dir ""))
          {"w" GridWorldDomain/ACTION_NORTH
           "s" GridWorldDomain/ACTION_SOUTH
           "a" GridWorldDomain/ACTION_WEST
           "d" GridWorldDomain/ACTION_EAST})
    (.initGUI exp)))



;;; --------------------------------------------------------------------------
(defn plot
  "Runs Q-Learning on the specified grid world, plotting trials and average
  performance."
  [gw]
  (let [tf (SinglePFTF. (PropositionalFunction/findPF (.generatePfs gw)
                                                      GridWorldDomain/PF_AT_LOCATION))
        rf (GoalBasedRF. (TFGoalCondition. tf) 5. -0.1)
        domain (.generateDomain (doto gw
                                     (.setTf tf)
                                     (.setRf rf)))
        hashing-factory (SimpleHashableStateFactory.)   ; Hashing system for looking up states
        q-learn-factory (reify LearningAgentFactory     ; Factory for Q-learning agent
                          (getAgentName [_]
                            "Q-learning")
                          (generateAgent [_]
                            (QLearning. domain 0.99 hashing-factory 0.3 0.1)))
        env (SimulatedEnvironment. domain
                                   (ConstantStateGenerator. (init-state)))
        exp (doto (LearningAlgorithmExperimenter. env 10 100 (into-array [q-learn-factory]))
                  (.setUpPlottingConfiguration 500 250 2 1000
                                               TrialMode/MOST_RECENT_AND_AVERAGE
                                               (into-array [PerformanceMetric/CUMULATIVE_STEPS_PER_EPISODE
                                                            PerformanceMetric/AVERAGE_EPISODE_REWARD])))]
    ;; Begin the experiment in the simulated learning environment
    (.startExperiment exp)))



;;; --------------------------------------------------------------------------
(defn -main
  "Entry point for Gridworld and getting started with BURLAP."
  [& args]
  (println "Hello, Gridworld!")
  (let [gw  (init-world)
        fun (if (some #{:plot} args) plot hello)]
    (fun gw)))

