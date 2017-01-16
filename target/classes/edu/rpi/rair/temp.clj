{G1 {:priority 6.0
     :state    [(not (open (door room1)))]}

 G2 {:priority 6.0
     :state    [(in prisoner room1)]}

 G3 {:priority 6.0
     :state    [(forall [?room]
                        (if (in prisoner ?room)
                          (in self ?room)))]}
 G4 {:priority 3.0
     :state    [(in prisoner room2)
                (in self room2)]}
 G5 {:priority 2.0

     :state    [(interrogates commander prisoner)]}}