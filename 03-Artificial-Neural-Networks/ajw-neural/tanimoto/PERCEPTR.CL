;;; PERCEPTR.CL
;;; Perceptron simulation

;;; (C) Copyright 1995 by Steven L. Tanimoto.
;;; This program is described in Chapter 13 ("Neural Networks") of
;;; "The Elements of Artificial Intelligence Using Common Lisp," 2nd ed.,
;;; published by W. H. Freeman, 41 Madison Ave., New York, NY 10010.
;;; Permission is granted for noncommercial use and modification of
;;; this program, provided that this copyright notice is retained
;;; and followed by a notice of any modifications made to the program.

;;; The following globals are used to improve program clarity,
;;; rather than efficient compilability.

;;; 4 inputs, 1 output.
(defparameter *num-inputs* 4)
(defvar *weights*)
(defparameter *threshold* 10.0)
(defvar *total-updates*)
(defvar *iterations*)
(defvar *positives*)
(defvar *negatives*)

;;; We train our perceptron to recognize "cost-effective" computers.

;;; 7 examples:
;;;   Model   Time on benchmark   Physical weight  Warantee Per.  Cost
;;;   Acme-1       5.0               7 lb           90 days     $ 2000
;;;   Summit-95    9.0               3             180             450
;;;   Super-2000   1.0             200             730           99000
;;;   Econo-001   20.0              30               0             300
;;;   Bright-10    7.0               8              15            1000
;;;   Turbox-2     4.0              10              90            3000
;;;   Timbuk-2    15.0              12              10            1500

;;; We declare that the cost-effective members of this set are:
;;;  Acme-1, Summit-95, Bright-10, and Turbox-2.
;;; Now here is the perceptron:

(setf *weights* '(0.0 0.0 0.0 0.0))

;;; Here are the input examples:

(setf acme-1 '(5.0 7.0 90 2000))
(setf summit-95 '(9.0 3.0 180 450))
(setf super-2000 '(1.0 200.0 730 99000))
(setf econo-001 '(20.0 30.0 0 300))
(setf bright-10 '( 7.0 8.0 15 1000))
(setf turbox-2 '( 4.0 10.0 90 3000))
(setf timbuk-2 '(15.0 12.0 10 1500))

(setf *positives* '(acme-1 summit-95 bright-10 turbox-2))
(setf *negatives* '(super-2000 econo-001 timbuk-2))

;;; TRAIN takes the EXAMPLE and adjusts the weights of the
;;; perceptron by DELTA at a time, until the example is classified
;;; positively (if POS-NEG is '+) or negatively (if POS-NEG is '-).
;;; If DELTA is small, TRAIN may use many iterations to converge.
(defun train (example pos-neg delta)
  "Repeatedly adjusts *WEIGHTS* until EXAMPLE is
   classified correctly."
  (setf *iterations* 0)
  (loop (if (eq (classify example) pos-neg) (return))
    (dotimes (i *num-inputs*)
      (setf (elt *weights* i)
            (funcall pos-neg (elt *weights* i)
                             (* delta (elt example i)) ) ) )
    (incf *total-updates*)
    (incf *iterations*) )
  (format t "*iterations*: ~S.~%" *iterations*) )

;;; CLASSIFY uses the current weights to decide whether
;;; the given input example should be accepted (+) or
;;; rejected (-).
(defun classify (example)
  "Returns '+ if the EXAMPLE is classified by the
   current weights as positive; '- otherwise."
  (if (< (inner-product example *weights*) *threshold*)
      '- '+) )

;;; INNER-PRODUCT computes the sum of the componentwise
;;; products for sequences X and Y.
(defun inner-product (x y)
  "Returns the vector inner product for X and Y."
  (if (or (null x)(null y)) 0.0
      (+ (* (first x)(first y))
         (inner-product (rest x)(rest y)) ) ) )


; note NEQ is already defined in Mac Allegro CL, but not in CLtL.
;(defun neq (x y) (not (eq x y)))

;;; ALLTRAIN attempts to use each of the positive and negative
;;; examples to train the perceptron.  Note that this only makes
;;; one pass through the examples, and multiple passes are often
;;; necessary before all examples can be correctly classified
;;; using the same set of weights.
(defun alltrain (delta)
  "Makes a training pass through all the examples."
  (dolist (x *positives*) (train (eval x) '+ delta))
  (dolist (x *negatives*) (train (eval x) '- delta)) )

;;; TESTALL returns T if all examples are classified correctly
;;; using the current weighting values.  It throws NIL as soon
;;; as it finds any example that is misclassified.
(defun testall ()
  "Returns T if all examples are correctly classified using
   the current weights."
  (catch 'fail (progn
    (dolist (x *positives*)
      (if (eq (classify (eval x)) '-) (throw 'fail nil) ) )
    (dolist (x *negatives*)
      (if (eq (classify (eval x)) '+) (throw 'fail nil) ) )
    T) ) )

(defun print-weights (weights)
  "Neatly prints WEIGHTS."
  (mapcar #'(lambda (w) (format t "~8,3F " w))
          weights)
  (terpri) )

;;; LEARNALL repeatedly calls ALLTRAIN until TESTALL returns T.
;;; Note that LEARNALL will go into an infinite loop if the
;;; sets of positive and negative examples cannot be handled
;;; correctly by any one-layer perceptron.
;;; LEARNALL also prints out counts of the training steps,
;;; for each run of TESTALL and for the entire LEARNALL run.
;;; Note that the strategy used here is to reduce DELTA by
;;; 20 percent in each top-level loop iteration.
(defun learnall ()
  "Keeps calling ALLTRAIN and TESTALL until all examples
   are correctly classified."
  (let ((epochs 0)
        (delta 0.5))
    (setf *total-updates* 0)
    (loop (if (testall) (return))
      (incf epochs)
      (format t "Beginning epoch ~5D.~%" epochs)
      (alltrain delta)
      (format t "Total updates: ~S~%" *total-updates*)
      (format t "Current weights:~%")
      (print-weights *weights*)
      (setf delta (* delta 0.8)) )
     ) )

(learnall)

