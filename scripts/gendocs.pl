#!/usr/bin/env perl

use strict;
use warnings;

use HTML::Entities;
use File::Basename;
use Getopt::Long;
use Pod::Usage;

my $package = 'org/mosspaper/objects';
my $TMP_OUT_DIR = '/tmp/moss-docs';
my $CONFIG_JAVA = 'src/org/mosspaper/Config.java';
my $CONFIG_DOC = "$TMP_OUT_DIR/org/mosspaper/Config.html";

sub load_config {
    my $config_lookup = {};
    open(JAVA, "<$CONFIG_JAVA");
    while (<JAVA>) {
        if ($_ =~ m/(CONF_\w+)\s+=\s+"(\w+)";/) {
            $config_lookup->{$1} = { 
                var_name => $2
            } ;
        }
    }
    close(JAVA);
    return $config_lookup;
}

sub load_objects {
    print "Loading TextObjects\n";
    my $obj_lookup = ();
    open(JAVA, "<src/$package/TextObjects.java");
    while (<JAVA>) {
        if ($_ =~ m/TEXT_OBJECTS.put\(\s*"(\w+)"\s*,.*\((\w+)\.class\).*\)/) {
            $obj_lookup->{$2} = $1;
        }
    }
    close(JAVA);
    return $obj_lookup;
}

sub run_javadoc {
    print "Running javadoc\n";
    my $files = join(' ', split(/\n/, `find src/org/mosspaper/objects -type f -not -name '.*.swp'`));
    mkdir $TMP_OUT_DIR;
    my $ec = system("javadoc -d $TMP_OUT_DIR $files $CONFIG_JAVA 1>/dev/null 2>&1 ");
    if ($ec != 0) {
        print "Javadoc failed with $ec: exiting.\n";
        exit 1;
    }
    return 1;
}

sub parse_config {
    my ($config) = @_;
    my $config_file = slurp($CONFIG_DOC);
    if ($config_file =~ m/<!-- ==+ FIELD DETAIL ==+ -->(.*)<!-- ==+ CONSTRUCTOR DETAIL ==+ -->.*/ism) {
        my $fields = $1;
        for my $key (keys %{ $config }) {
            my $h = $config->{$key};
            my $re = '<h3>\s*' . $key . '\s*</h3>.*?<dd>(.*?)<p>';
            if ($fields =~ m{$re}ism) {
                $h->{desc} = $1;
            }
        }
    }
}

sub parse_files {
    print "Parsing documentation\n";
    my ($objs, $lookup) = @_;
    my @files = `find ${TMP_OUT_DIR}/${package} -type f -name '*.html' `;
    for my $f (@files) {
        my ($n, $p, $s) = fileparse($f, qr/\.[^.]*/);
        my $body = slurp($f);

        next if not exists $lookup->{$n};
        my $name = $lookup->{$n};
        $objs->{$name} = () if not exists $objs->{$name};
        if ($body =~ m/<!-- ==+ CONSTRUCTOR DETAIL ==+ -->(.*)<!-- ==+ METHOD DETAIL ==+ -->.*/ism) {
            parse_constructors($objs, $name, $1);
        }
    }
}

sub parse_constructors {
    my ($objs, $name, $body) = @_;
    my $conlist = (); # list of constructors
    my @cons = split(/<h3>.*?<\/h3>/ism, $body);
    shift @cons; # ignore the first item in list
    for my $c (@cons) {
        my %data = ();
        if ($c =~ m{<dd>(.*?)<p>}ism) {
            $data{desc} = strip($1);
        } else {
            $data{desc} = '';
        }
        my @params = ();
        while ($c =~ m{DD><CODE>(.*?)</CODE>\s+-\s+(.*?)<}gism) {
            push @params, [ $1, $2 ] ;
        }
        $data{params} = \@params;
        push @{ $conlist }, \%data;
    }
    $objs->{$name} = $conlist;
}

sub strip {
    my $str = shift;
    $str =~ s/^\s+//g;
    $str =~ s/\s+$//g;
    $str =~ s/&nbsp;//g;
    return decode_entities($str);
}

sub slurp {
    my $f = shift;
    my $body = '';

    open(HTML, "<$f");
    $body .= $_ while (<HTML>);
    close(HTML);

    return $body;
}

sub uncommitted_changes {
    my $mods = `git status -s`;
    return length($mods) > 0;
}

my ($help, $sitedocs, $vimsyntax) = (0, 0, 0);

GetOptions('help' => \$help
         , 'sitedocs' => \$sitedocs
         , 'vimsyntax' => \$vimsyntax
       ) or pod2usage(2);
pod2usage(1) if $help;

if (uncommitted_changes()) {
    print "Uncomitted changes!?! I'm outta here!\n";
    exit 0;
}

my $objs = { };
my $config = load_config();
my $lookup = load_objects();
if (run_javadoc()) {
    parse_config($config);
    parse_files($objs, $lookup);
}

my $CONFIG_FILE = 'assets/config.html';
my $OBJECT_FILE = 'assets/objects.html';

my $config_html = '';
my $obj_html = '';

if ($sitedocs) {
    $CONFIG_FILE = 'config.html';
    $OBJECT_FILE = 'objects.html';

    $config_html .= qq{
        <table>
            <tr>
                <th>Variable</th>
                <th>Description</th>
            </tr>
    };
    for my $k (sort keys %{ $config }) {
        my %h = %{ $config->{$k} };
        my $var_name = $h{var_name};
        my $desc = $h{desc} || '';
        $config_html .= qq{
            <tr>
                <td><h3>$var_name</td>
                <td>$desc</td>
            </tr>
        }
    }
    $config_html .= "\n</table>\n";

    $obj_html .= qq{
        <table>
            <tr>
                <th>Object</th>
                <th>Parameters</th>
                <th>Description</th>
            </tr>
    };
    for my $k (sort keys %{ $objs }) {
        for my $c (@{ $objs->{$k} }) {
            my $desc = $c->{desc};
            my @params = @{ $c->{params} };
            my $phdr = '';
            my $pbody = '';

            if ($#params + 1 > 0) {
                $pbody .= '<ul>';
                for my $p (@params) {
                    my $name = $p->[0];
                    my $desc = $p->[1];
                    $phdr .= "$name, ";
                    $pbody .= "<li>$name - $desc</li>";
                }
                $phdr =~ s/\s*,\s*$//g;
                $phdr = "($phdr)";
                $pbody .= '</ul>';
            }

            $obj_html .= qq{
                <tr>
                    <td><h3>$k</h3></td>
                    <td>$phdr</td>
                    <td>
                        <p>$desc</p>
                        <p>$pbody</p>
                    </td>
                </tr>
            };
        }
    }
    $obj_html .= "\n</table>\n";
    # checkout gh-pages
    `git checkout gh-pages`
} if ($vimsyntax) {
    my $INDENT = "\n    \\";
    my $config_html = $INDENT;
    my $obj_html = $INDENT;

    my $total = 0;
    for my $k (sort keys %{ $config }) {
        my %h = %{ $config->{$k} };
        my $var_name = $h{var_name};
        if ($total > 70) {
            $config_html .= $INDENT;
            $total = 0;
        }
        $config_html .= "$var_name ";
        $total += length($var_name) + 1;
    }

    $total = 0;
    for my $k (sort keys %{ $objs }) {
        if ($total > 70) {
            $obj_html .= $INDENT;
            $total = 0;
        }
        $obj_html .= "$k ";
        $total += length($k) + 1;
    }
    print $config_html, "\n";
    print $obj_html, "\n";
    exit;
} else {
    for my $k (sort keys %{ $config }) {
        my %h = %{ $config->{$k} };
        my $var_name = $h{var_name};
        my $desc = $h{desc} || '';
        $config_html .= qq{
            <div>
                <h3>$var_name</h3>
                <p>
                $desc
                </p>
                <hr />
            </div>
        }
    }

    for my $k (sort keys %{ $objs }) {
        for my $c (@{ $objs->{$k} }) {
            my $desc = $c->{desc};
            my @params = @{ $c->{params} };
            my $phdr = '';
            my $pbody = '';

            if ($#params + 1 > 0) {
                $pbody .= 'Parameters: <ul>';
                for my $p (@params) {
                    my $name = $p->[0];
                    my $desc = $p->[1];
                    $phdr .= "$name, ";
                    $pbody .= "<li>$name - $desc</li>";
                }
                $phdr =~ s/\s*,\s*$//g;
                $phdr = "($phdr)";
                $pbody .= '</ul>';
            }

            $obj_html .= qq{
                <div>
                    <h3><a name="$k">$k</a>$phdr</h3>
                    <p>
                    $desc
                    </p>
                    $pbody
                    <hr />
                </div>
            };
        }
    }
}

my $file = slurp($CONFIG_FILE);
$file =~ s/(^\s*<!-- CONFIG BEGIN -->\s*$).*(^\s*<!-- CONFIG END -->\s*$)/$1$config_html\n$2/ism;

open(CONFIG, ">$CONFIG_FILE");
print CONFIG $file;
close(CONFIG);

$file = slurp($OBJECT_FILE);
$file =~ s/(^\s*<!-- OBJECTS BEGIN -->\s*$).*(^\s*<!-- OBJECTS END -->\s*$)/$1$obj_html\n$2/ism;
open(OBJECTS, ">$OBJECT_FILE");
print OBJECTS $file;
close(OBJECTS);

# If we are not on the master branch any longer, return
if ($sitedocs) {
    `git add config.html objects.html`;
    `git commit -m 'Updated online documentation.'`;
    `git checkout master`;
}


__END__

=head1 NAME

gendocs.pl

=head1 SYNOPSIS

B<gendocs.pl> S<[ B<-h> ]> S<[ B<-d> ]>

=head1 DESCRIPTION

Generate documentation for the moss android application or the moss site.

=head1 OPTIONS

=over 4

=item B<-h>, B<--help>

Help: Print the usage.

=item B<-s>, B<--site>

Generate the documentation for the moss site. The default is to generate html
for the internal help pages.

=back

=head1 AUTHOR

Tim Horton

=cut

