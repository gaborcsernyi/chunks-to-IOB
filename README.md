# chunks-to-IOB
A conversion tool to convert a corpora tagged for chunks inline to IOB format (column structure: token, POS-tag, IOB-tag). 


GENERAL DESCRIPTION
-------------------

This script was originally created to convert the BNC1000 copus by Nicholson and Baldwin [1] 
to IOB format with respect to noun compounds (and multiword named entities).

The input of the script is a text file in which a single sentence from the BNC1000 takes 3 lines:
	i.   raw sentence without annotations;
	ii.  sentence annotated for noun compounds (and/or multiword named entities);
	iii. sentence with POS tags;

In other words, the sentences from the BNC1000 corpus are stored in every 3rd line starting from line #2,
before which the raw sentence (i.e. sentence without annotations for compounds and/or multiword named 
entities) is stored, and after which the POS-tagged version of the sentence can be found.


ADDITIONAL NOTES
----------------

This format  can simply be generated by the following Python script (the input of which is the BNC1000):

	import nltk
	import os
	import re

	in1 = open("/home/gabor/BNC1000/Baldwin-norm.xml", "r")
	out1 = open("/home/gabor/BNC1000/Baldwin-norm" + ".proc", "w")
	sentences = in1.readlines()
	tmp = ""
	for sent in sentences:
		sent = re.sub('\r\n', '', sent)
		sent = re.sub(' +', ' ', sent)
		sent = sent.replace("\$", "$")
		sent = sent.replace("---", "--")
		sent = sent.replace("&amp;", "&")
		sent = sent.strip()
		print >>out1, re.sub('<[^>]*>', '', sent)
		print >>out1, sent
		print >>out1, nltk.pos_tag(nltk.word_tokenize(re.sub('<[^>]*>', '', sent)))

	in1.close()
	out1.close()

	print "processing completed"


(In this example the default tokenizer and the default POS tagger of NLTK are used to provide the 
POS-tagged version of the sentences. In addition, some basic text-normalization is also included
with the help of regular expressions.)


EXAMPLES
--------

Example for input of the script:

	Things are interesting on the slot front, too.						  						                  => i.
	Things are interesting on the <cn rel="NA" hvf="front">slot front</cn> , too .		  		  => ii.
	[('Things', 'NNS'), ('are', 'VBP'), ('interesting', 'VBG'), ('on', 'IN'), ('the', 'DT'),
	('slot', 'NN'), ('front', 'NN'), (',', ','), ('too', 'RB'), ('.', '.')]			  			      => iii.


Example for output of the script:

	Things		  NNS	O
	are			    VBP	O
	interesting	VBG	O
	on			    IN	O
	the		    	DT	O
	slot		    NN	B-NP
	front		    NN	I-NP
	,	          ,		O
	too	        RB	O
	.	          .		O

---------------------------------------------------------------------------------------------------------

REFERENCES
----------

[1] Jeremy Nicholson and Timothy Baldwin. 2008. Interpreting Compound Nominalisations. In LREC 2008 
Workshop: Towards a Shared Task for Multiword Expressions (MWE 2008), pages 43–45, Marrakech, Morocco.
