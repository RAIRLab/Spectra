
;This is James' roses problem from the PERI.2 symbol grounding problem paper

{:name "roses"
    :background [
      (agent n) (agent w) (agent s) (agent w)
      (pos p1) (pos p2) (pos p3) (pos p4) (pos p5) 
      (pos p6) (pos p7) (pos p8) (pos p9)
      (rose r1) (rose r2) (rose r3)
    ]

    :actions [
      (define-action remove [?r ?p] {
        :preconditions [
          (at ?r ?p)
        ]
        :additions [
          (forall (a) (if (agent a) (Perceives! a (not (at ?r ?p)))))
        ]
        :delitions [
          (at ?r ?p)
        ]
      })
    ]

    :start [
      ;Initial positions of the roses
      (at r1 p1) (at r2 p5) (at r3 p9)

      ;All agents know the initial positions of the roses
      (Knows! n (at r1 p1)) (Knows! n (at r2 p5)) (Knows! n (at r3 p9))
      (Knows! w (at r1 p1)) (Knows! w (at r2 p5)) (Knows! w (at r3 p9))
      (Knows! s (at r1 p1)) (Knows! s (at r2 p5)) (Knows! s (at r3 p9))
      (Knows! e (at r1 p1)) (Knows! e (at r2 p5)) (Knows! e (at r3 p9))
      
      ;Only r3 is a faded rose, the rest are not faded
      (faded r3)

      ;All agents know rose 3 is faded
      (Knows! n (faded r3)) (Knows! w (faded r3)) (Knows! s (faded r3)) (Knows! e (faded r3))
    ]

    :goal [
      (forall (a r p) 
        (if (and (agent a) (rose r) (faded r) (pos p))
          (Knows! a (not (at r p)))
        )
      )
    ]
}


