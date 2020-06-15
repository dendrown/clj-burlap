(ns gridworld.core
  (:import (burlap.domain.singleagent.gridworld GridWorldDomain
                                                GridWorldVisualizer)
           (burlap.domain.singleagent.gridworld.state GridAgent
                                                      GridLocation
                                                      GridWorldState)
           (burlap.shell.visual VisualExplorer))
  (:gen-class))



;;; --------------------------------------------------------------------------
(defn hello
  "Launches a GUI with a grid world."
  []
  (let [gw (doto (GridWorldDomain. 11 11)                   ; 11x11 grid world
                 (.setMapToFourRooms)                       ; four-room layout
                 (.setProbSucceedTransitionDynamics 0.8))   ; stochastic transition success rate
        domain (.generateDomain gw)                         ; generate the grid world domain
        s (GridWorldState. (GridAgent. 0 0)
                           [(GridLocation. 10 10 "loc0")])
        v (GridWorldVisualizer/getVisualizer (.getMap gw))
        exp (VisualExplorer. domain v s)]

    ;; set control keys to use w-s-a-d
    (run! (fn [[code dir]]
            (.addKeyAction exp code dir ""))
          {"w" GridWorldDomain/ACTION_NORTH
           "s" GridWorldDomain/ACTION_SOUTH
           "a" GridWorldDomain/ACTION_WEST
           "d" GridWorldDomain/ACTION_EAST})
    (.initGUI exp)))



;;; --------------------------------------------------------------------------
(defn -main
  "Entry point for Gridworld and getting started with BURLAP."
  [& args]
  (println "Hello, Gridworld!")
  (hello))
