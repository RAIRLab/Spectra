{
 :name "test 1"
 :background [ (forall (?x ?y ?room1 ?room2)
                       (if (and (Interrogates ?x ?y)
                                (In ?x ?room1)
                                (In ?y ?room2))
                         (= ?room1 ?room2)))

              (In commander room2)
              ()
              ]
 :start [Be_In_Room
         (Closed (door room1))
         (Accompany self prisoner)]

 :goalSequence [
                [G1 1 [(In prisoner1 room1)]]
                [G2 1 [(Closed (door room1))]]
                [G3 1 [(Accompany self prisoner)]]

                [G4 2 [(Interrogates command robot)]]
                [G5 2 []]

                ]


 }