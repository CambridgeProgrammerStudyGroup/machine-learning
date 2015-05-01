;;; BACKPROP.CL
;;; Demonstration of backpropagation in a feedforward neural network.

;;; (C) Copyright 1995 by Steven L. Tanimoto.
;;; This program is described in Chapter 13 ("Neural Networks") of
;;; "The Elements of Artificial Intelligence Using Common Lisp," 2nd ed.,
;;; published by W. H. Freeman, 41 Madison Ave., New York, NY 10010.
;;; Permission is granted for noncommercial use and modification of
;;; this program, provided that this copyright notice is retained
;;; and followed by a notice of any modifications made to the program.

;;; In this example, the net learns to distinguish between
;;; horizontal and vertical patterns.

;;; With *ETA* set to 0.1 and fixed, the 16-example training set
;;; gets convergence to a system error of 0.02 in about 500 epochs.
;;; This program runs on a Macintosh IIci with MCL at the rate of about 200
;;; epochs every 5 minutes.  Therefore 1000 epochs requires about 25min.

;;; Learning proceeds by running the backpropagation algorithm on
;;; each training example, accumulating the proposed weight changes
;;; in an array,  "increments".  After each example has been processed,
;;; the "epoch" ends and the weights are updated using the increments.
;;; The main procedure shows a display of the weights and activation
;;; levels every 100 epochs.

;;; The program computes the "system error" in each iteration, which is
;;; the average squared error on the examples in the training set.
 
(defparameter *NINPUTS* 9)
(defparameter *NHIDDEN* 5)
(defparameter *NOUTPUTS* 2)
(defparameter *NMAX* 10)
;;; Set *NMAX* as the maximum of
;;;   (*NINPUTS* + 1), (*NHIDDEN* + 1), and *NOUTPUTS*.
;;; This is because the input layer and the hidden layer each have
;;; a "dummy unit" whose activation is always 1, which feeds the units
;;; of the next layer in lieu of their having a "threshold" or "bias".
;;; The weights for the threshold links are learned just like all
;;; the others.

(defparameter *NEXAMPLES* 16)
(defparameter *BETA* 1.0)
(defparameter *ETA-START* 0.1)
(defparameter *ETA-RUN-THRESH* 5)

(defvar *w*)
(setf *w* (make-array (list 2 *nmax* *nmax*)
                    :element-type 'float) )
;;; Each weight is associated with the level of its tail endpoint,
;;; and is thus referenced with with an expression of them form
;;; (AREF *W* level from-node to-node).

(defvar *increment*)
(setf *increment*
      (make-array (list 2 *nmax* *nmax*)
                  :element-type 'float) )
;;; Used to accumulate the corrections to weights during an epoch.

(defvar *activation*)
(setf *activation*
      (make-array (list 3 *nmax*)
                  :element-type 'float) )
;;; Unit activations ( = g(h) ).

(defvar *h*)
(setf *h* (make-array (list 3 *nmax*)
                      :element-type 'float) )
;;; Used to store the summed inputs to each unit.

(defvar *delta*)
(setf *delta*
      (make-array (list 3 *nmax*)
                  :element-type 'float) )
;;; Used during backpropagation.

(defvar *gp*)
(setf *gp* (make-array (list 3 *nmax*)
                       :element-type 'float) )
;;; Used to store the derivative of G, that is, G prime,
;;; evaluated on the H value of each node.

(defvar *levelsize*)
(setf *levelsize*
      (make-array '(3) :element-type 'integer) )

(defvar *simlevelsize*)
(setf *simlevelsize*
      (make-array '(3) :element-type 'integer) )

(defvar *eta*)

;;; Here is the training set.
;;; Each input vector is given on a separate line.

(defun make-input-example (lst)
  "Returns its arguments in an array."
  (make-array (list *ninputs*)
    :element-type 'float :initial-contents lst) )

(defparameter *example-inputs*
   (make-array (list *nexamples*)
     :initial-contents (mapcar #'make-input-example '(

;;; vertical striping examples...
  (0.0 0.0 1.0   0.0 0.0 1.0   0.0 0.0 1.0)
  (0.0 1.0 0.0   0.0 1.0 0.0   0.0 1.0 0.0)
  (0.0 1.0 1.0   0.0 1.0 1.0   0.0 1.0 1.0)
  (1.0 0.0 0.0   1.0 0.0 0.0   1.0 0.0 0.0)
  (1.0 0.0 1.0   1.0 0.0 1.0   1.0 0.0 1.0)
  (1.0 1.0 0.0   1.0 1.0 0.0   1.0 1.0 0.0)

;;; horizontal striping examples... 
  (0.0 0.0 0.0     0.0 0.0 0.0   1.0 1.0 1.0)
  (0.0 0.0 0.0     1.0 1.0 1.0   0.0 0.0 0.0)
  (0.0 0.0 0.0     1.0 1.0 1.0   1.0 1.0 1.0)
  (1.0 1.0 1.0     0.0 0.0 0.0   0.0 0.0 0.0)
  (1.0 1.0 1.0     0.0 0.0 0.0   1.0 1.0 1.0)
  (1.0 1.0 1.0     1.0 1.0 1.0   0.0 0.0 0.0)

;;; Non striped examples...
  (0.0 0.0 0.0     0.0 0.0 0.0   0.0 0.0 0.0)
  (1.0 1.0 1.0     1.0 1.0 1.0   1.0 1.0 1.0) 
  (1.0 0.0 1.0     0.0 1.0 0.0   1.0 0.0 1.0)
  (0.0 1.0 0.0     1.0 0.0 1.0   0.0 1.0 0.0)
 ) ) ) )

(defun make-output-example (lst)
  "Returns its arguments in an array."
  (make-array (list *noutputs*)
    :element-type 'float :initial-contents lst) )

(defparameter *example-outputs*
   (make-array (list *nexamples*)
     :initial-contents (mapcar #'make-output-example '(
  (1.0 0.0)
  (1.0 0.0)
  (1.0 0.0)
  (1.0 0.0)
  (1.0 0.0)
  (1.0 0.0)

  (0.0 1.0)
  (0.0 1.0)
  (0.0 1.0)
  (0.0 1.0)
  (0.0 1.0)
  (0.0 1.0)

  (0.0 0.0)
  (0.0 0.0)
  (0.0 0.0)
  (0.0 0.0)
   ) ) ) )



(defun init ()
  "Initializes the *LEVELSIZE* arrays and the weights."
  (setf (aref *levelsize* 0) (1+ *ninputs*))
  (setf (aref *levelsize* 1) (1+ *nhidden*))
  (setf (aref *levelsize* 2) *noutputs*)
  ;;; Note that the input layer and the hidden layer each have an
  ;;; extra unit, whose activation is always set to 1, and whose
  ;;; outgoing weights serve as thresholds for other units.
  (setf (aref *activation* 0 *ninputs*) 1.0)
  (setf (aref *activation* 1 *nhidden*) 1.0)
  ;;; The simulated numbers of units are kept, also:
  (setf (aref *simlevelsize* 0) *ninputs*)
  (setf (aref *simlevelsize* 1) *nhidden*)
  (setf (aref *simlevelsize* 2) *noutputs*)
  ;;; Now initialize the weights to small random values:
  (dotimes (level 2)
    (dotimes (i (aref *levelsize* level))
      (dotimes (j (aref *simlevelsize* (1+ level)))
        (setf (aref *w* level i j)
              (/ (random 100) 500.0) )
     ) ) )
  ;;; Initialize the step size for weight adjustment:
  (setf *eta* *eta-start*)
  )

(defun g (h)
  "Returns the value of the sigmoid function at H."
  (/ (1+ (exp (* -2.0 *beta* h)))) )

(defun feedforward (input-example)
  "Determines unit activations for INPUT-EXAMPLE."
  (let (sum gval (p input-example))
    ;;; Copy input activations:
    (dotimes (i *ninputs*)
      (setf (aref *activation* 0 i) (aref p i)) )
    ;;; Compute activations at next 2 levels:
    (dotimes (level 2)
      (dotimes (j (aref *simlevelsize* (1+ level)))
        (setf sum 0.0)
        (dotimes (i (aref *levelsize* level))
          (incf sum (* (aref *activation* level i)
                       (aref *w* level i j) )) )
        (setf (aref *h* (1+ level) j) sum)
        (setf gval (g sum))
        (setf (aref *activation* (1+ level) j) gval)
        (setf (aref *gp* (1+ level) j)
              (* 2.0 *beta* gval (- 1.0 gval)) )
        ) ) ) )

;;; The following procedure takes one input/output pair,
;;; determines the error at each output using the current
;;; weights, and uses backpropagation to compute the changes
;;; that should be made to the weights.  These changes are
;;; added to the pre-existing collection of changes, maintained
;;; in the array *INCREMENT*.
(defun backprop-one-example (input-example desired-output)
  "Uses one I/O example to adjust the weights."
  (let (sum example-error temp)
    (feedforward input-example)
    ;;; Compute *DELTA* values for output layer:
    (dotimes (i *noutputs*)
      (setf (aref *delta* 2 i)
        (* (aref *gp* 2 i)
           (- (aref desired-output i)
              (aref *activation* 2 i) )) ) )
    (let ((level 1))
      ;;; Compute *INCREMENT* values for arcs coming
      ;;; into output layer:
      (dotimes (i (aref *levelsize* level))
        (setf sum 0.0)
        (dotimes (j (aref *simlevelsize* (1+ level)))
          (incf sum (* (aref *w* level i j)
                       (aref *delta* (1+ level) j) ))
          (incf (aref *increment* level i j)
            (* *eta*
               (aref *delta* (1+ level) j)
               (aref *activation* level i) ) ) )
         ;;; Compute *DELTA* values for hidden layer:
         (if (not (= i *nhidden*))
           (setf (aref *delta* level i)
                 (* (aref *gp* level i) sum) ) ) )
      (setf level 0)
      ;;; Compute *INCREMENT* values for hidden layer's
      ;;; incoming arcs:
      (dotimes (i (aref *levelsize* level))
        (setf sum 0.0)
        (dotimes (j (aref *simlevelsize* (1+ level)))
          (incf sum (* (aref *w* level i j)
                       (aref *delta* (1+ level) j) ))
          (incf (aref *increment* level i j)
            (* *eta*
               (aref *delta* (1+ level) j)
               (aref *activation* level i) ) ) ) ) )

    ;;; Compute the sum-squared error for this example:
    (setf example-error 0.0)
    (dotimes (i *noutputs* example-error)
      (setf temp (- (aref desired-output i)
                    (aref *activation* 2 i) ))
      (incf example-error (* temp temp)) ) ) )

(defun show-weights ()
  "Displays the current weights."
  (dotimes (level 2)
    (format t "~%Incoming weights for level ~2D:" (1+ level))
    (dotimes (j (aref *simlevelsize* (1+ level)))
      (format t "~%  For unit (~2d,~2d):~%"
        (1+ level) j)
      (dotimes (i (aref *levelsize* level))
        (format t "~8,4F, " (aref *w* level i j)) )
    ) ) )

(defun show-activations ()
  "Displays the current unit activation levels."
  (dotimes (level 2)
    (dotimes (j (aref *simlevelsize* (1+ level)))
      (format t "Activation for node (~2d,~2d) = ~6,3F.~%"
        (1+ level) j (aref *activation* (1+ level) j) ) ) )
  ;;; Report the input and output values:
  (format t "The input vector is: ")
  (dotimes (i *ninputs*)
    (format t "~6,3F, " (aref *activation* 0 i)) )
  (format t "~%The output vector is: ")
  (dotimes (i *noutputs*)
    (format t " ~6,3F, " (aref *activation* 2 i)) )
 )

(defun clear-increments ()
  "Sets the increments to zero for the beginning
   of a new epoch."
  (dotimes (level 2)
    (dotimes (i *nmax*)
      (dotimes (j *nmax*)
        (setf (aref *increment* level i j) 0.0) ) ) ) )

(defun apply-increments ()
  "Changes the weights according to the increments computed
   during the epoch."
  (dotimes (level 2)
    (dotimes (i (aref *levelsize* level))
      (dotimes (j (aref *simlevelsize* (1+ level)))
        (incf (aref *w* level i j)
              (aref *increment* level i j) ) ) ) ) )

(defun training-epoch ()
  "Makes a pass through all the training examples,
   accumulating the increments to the weights until the end,
   and finally adjusts the weights.
   Returns the average system error for the examples."
  (clear-increments)
  (let ((sum 0.0))
    (dotimes (i *nexamples*)
      (incf sum
            (backprop-one-example
              (aref *example-inputs* i)
              (aref *example-outputs* i) ) ) )
    (apply-increments)
    (/ sum *nexamples*) ) )


(defun backprop ()
  "Performs training of the neural network for many epochs."
  (format t "Beginning neural net training with backpropagation.~%")
  (init)
  (show-weights)
  (let ((num-consec-wins 0) ; number of consecutive epochs with improvements.
        (last-error 1.0)    ; an arbitrary high value.
        system-error )
    (dotimes (epoch 5000)
      (setf system-error (training-epoch))

      ;;;   Adaptively modify eta to improve training rate:
      (if (< system-error last-error)
          (progn
            (incf num-consec-wins)
            (if (> num-consec-wins *ETA-RUN-THRESH*)
                (progn
                  (incf *eta* 0.01)
                  (setf num-consec-wins 0) ) ) )
          (progn
            (setf *eta* (* *eta* 0.8))
            (setf num-consec-wins 0) ) )

      (if (zerop (mod epoch 100))
          (progn
            (format t "~%At end of epoch # ~4d, system error = ~9,6F."
                    epoch system-error)
            (show-weights) ) )
      (setf last-error system-error) ) ) )


(backprop) ; Starts the whole program running.
